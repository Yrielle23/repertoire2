package framework;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import framework.annotation.ApiController;
import framework.annotation.GetMapping;
import framework.annotation.PostMapping;

@WebListener
public class ControllerScannerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<String, Mapping> routesGet = new HashMap<>();
        Map<String, Mapping> routesPost = new HashMap<>();

        String[] packagesToScan = {"controller", "controllers", "app.controller"};

        for (String packageName : packagesToScan) {
            System.out.println("[Framework] Scanning package: " + packageName);
            scanPackage(packageName, routesGet, routesPost);
        }

        sce.getServletContext().setAttribute("routesGet", routesGet);
        sce.getServletContext().setAttribute("routesPost", routesPost);

        System.out.println("[Framework] Routes GET: " + routesGet.size());
        System.out.println("[Framework] Routes POST: " + routesPost.size());
        System.out.println("[Framework] Registered routes: " + routesGet.keySet());
    }

    private void scanPackage(String packageName, Map<String, Mapping> routesGet, Map<String, Mapping> routesPost) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace(".", "/");
            URL resource = loader.getResource(path);

            if (resource == null) {
                System.out.println("[Framework] Package not found: " + packageName);
                return;
            }

            File directory = null;
            try {
                // Décode l'URL pour gérer les espaces et caractères spéciaux
                String decodedPath = URLDecoder.decode(resource.getFile(), "UTF-8");
                directory = new File(decodedPath);
            } catch (UnsupportedEncodingException e) {
                directory = new File(resource.getFile());
            }

            if (!directory.exists() || !directory.isDirectory()) {
                System.out.println("[Framework] Directory not found or not accessible: " + directory.getAbsolutePath());
                return;
            }

            File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
            if (files == null || files.length == 0) {
                System.out.println("[Framework] No .class files found in: " + directory.getAbsolutePath());
                return;
            }

            System.out.println("[Framework] Found " + files.length + " class files in " + packageName);

            for (File file : files) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);

                    // Scan uniquement les contrôleurs API
                    if (clazz.isAnnotationPresent(ApiController.class)) {
                        scanControllerMethods(clazz, routesGet, routesPost);
                    }

                } catch (ClassNotFoundException e) {
                    System.err.println("[Framework] Could not load class: " + className);
                } catch (Exception e) {
                    System.err.println("[Framework] Error processing class " + className + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("[Framework] Error scanning package " + packageName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void scanControllerMethods(Class<?> clazz, 
                                       Map<String, Mapping> routesGet, Map<String, Mapping> routesPost) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String url = mapping.value();
                routesGet.put(url, new Mapping(clazz.getName(), method.getName(), "GET"));
                System.out.println("[Framework] ✓ GET " + url + " -> " + clazz.getSimpleName() + "." + method.getName());
            }

            if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                String url = mapping.value();
                routesPost.put(url, new Mapping(clazz.getName(), method.getName(), "POST"));
                System.out.println("[Framework] ✓ POST " + url + " -> " + clazz.getSimpleName() + "." + method.getName());
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}