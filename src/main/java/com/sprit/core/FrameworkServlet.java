package com.sprit.core;

import com.sprit.routing.MethodeInfo;
import com.sprit.routing.UrlMethode;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import vue.Controller;
import vue.RequestMapping;

public class FrameworkServlet extends HttpServlet {

    private static final Map<UrlMethode, MethodeInfo> ROUTE_MAP = new LinkedHashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String controllerPackage = config.getInitParameter("controllerPackage");
        if (controllerPackage == null || controllerPackage.trim().isEmpty()) {
            controllerPackage = "controller";
        }

        try {
            scanControllers(controllerPackage);
        } catch (Exception e) {
            throw new ServletException("Échec du scan des contrôleurs", e);
        }
    }

    private void scanControllers(String basePackage) throws Exception {
        String packagePath = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if ("file".equals(protocol)) {
                Path directory = Paths.get(resource.toURI());
                scanDirectory(basePackage, directory);
            } else if ("jar".equals(protocol)) {
                scanJar(basePackage, resource);
            }
        }
    }

    private void scanDirectory(String basePackage, Path directory) throws Exception {
        if (!Files.exists(directory)) {
            return;
        }
        Files.walk(directory)
                .filter(path -> path.toString().endsWith(".class"))
                .forEach(path -> {
                    String className = buildClassName(basePackage, directory, path);
                    try {
                        registerControllerClass(className);
                    } catch (Exception e) {
                        System.err.println("[SPRIT Framework] Erreur pendant l'enregistrement du contrôleur " + className + ": " + e.getMessage());
                    }
                });
    }

    private void scanJar(String basePackage, URL resource) throws Exception {
        JarURLConnection connection = (JarURLConnection) resource.openConnection();
        try (JarFile jarFile = connection.getJarFile()) {
            String packagePath = basePackage.replace('.', '/') + "/";
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName
                            .replace('/', '.')
                            .replaceAll("\\.class$", "");
                    registerControllerClass(className);
                }
            }
        }
    }

    private String buildClassName(String basePackage, Path root, Path classFile) {
        Path relative = root.relativize(classFile);
        String withoutExtension = relative.toString().replace(".class", "");
        return basePackage + "." + withoutExtension.replace(System.getProperty("file.separator"), ".");
    }

    private void registerControllerClass(String className) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> controllerClass = Class.forName(className, false, classLoader);
        if (controllerClass.isAnnotationPresent(Controller.class)) {
            Object controller = controllerClass.getDeclaredConstructor().newInstance();
            registerControllerMethods(controller);
        }
    }

    private void registerControllerMethods(Object controller) {
        Class<?> controllerClass = controller.getClass();
        for (Method method : controllerClass.getDeclaredMethods()) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            if (mapping != null) {
                UrlMethode key = new UrlMethode(mapping.value(), mapping.method());
                ROUTE_MAP.put(key, new MethodeInfo(controller, method));
                System.out.println("[SPRIT Framework] Mapping enregistré : " + key + " -> "
                        + controllerClass.getSimpleName() + "." + method.getName());
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String uri = normalizeUri(req.getRequestURI(), req.getContextPath());
        String method = req.getMethod();
        UrlMethode lookup = new UrlMethode(uri, method);

        System.out.println("[SPRIT Framework] Requête interceptée : " + method + " " + uri);

        MethodeInfo handler = ROUTE_MAP.get(lookup);
        if (handler != null) {
            try {
                handler.invoke(req, resp);
                return;
            } catch (InvocationTargetException | IllegalAccessException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Erreur interne du framework : " + e.getMessage());
                return;
            }
        }

        renderNotFound(resp, uri, method);
    }

    private String normalizeUri(String uri, String contextPath) {
        if (uri == null) {
            return "/";
        }
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        return uri.isEmpty() ? "/" : uri;
    }

    private void renderNotFound(HttpServletResponse resp, String uri, String method) throws IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>404 - URL non supportée</title></head>");
        out.println("<body>");
        out.println("<h1>404 - URL non supportée</h1>");
        out.println("<p>URL demandée : " + uri + "</p>");
        out.println("<p>Méthode : " + method + "</p>");
        out.println("<h2>Routes supportées :</h2>");
        out.println("<ul>");
        for (Map.Entry<UrlMethode, MethodeInfo> entry : ROUTE_MAP.entrySet()) {
            UrlMethode key = entry.getKey();
            MethodeInfo info = entry.getValue();
            out.println("<li>" + key.getMethod() + " " + key.getUrl() + " -> "
                    + info.getController().getClass().getSimpleName() + " -> "
                    + info.getAction().getName() + "</li>");
        }
        out.println("</ul>");
        out.println("</body>");
        out.println("</html>");
    }
}
