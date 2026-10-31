package mg.itu.mvc.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.mvc.annotation.Controller;
import mg.itu.mvc.annotation.Url;
import mg.itu.mvc.bind.ParameterBinder;
import mg.itu.mvc.file.FileUpload;
import mg.itu.mvc.session.Session;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontController", urlPatterns = {
    "/", 
    "/upload", 
    "/session-set", 
    "/session-get",
    "/session",
    "/session/login",
    "/session/logout"
}, loadOnStartup = 1)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize       = 20L * 1024 * 1024,
    maxRequestSize    = 25L * 1024 * 1024
)
public class FrontController extends HttpServlet {

    private final Map<String, Route> routes = new HashMap<>();
    private final Map<Class<?>, Object> controllersInstances = new HashMap<>();

    private record Route(Object controller, Method method) {}

    @Override
    public void init() throws ServletException {
        try {
            List<Class<?>> controllers = List.of(
                Class.forName("mg.itu.controller.UploadController"),
                Class.forName("mg.itu.controller.TestController")
            );

            for (Class<?> c : controllers) {
                if (!c.isAnnotationPresent(Controller.class)) continue;

                Object instance = controllersInstances.computeIfAbsent(c, k -> {
                    try {
                        return k.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                for (Method m : c.getDeclaredMethods()) {
                    Url url = m.getAnnotation(Url.class);
                    if (url == null) continue;
                    String key = url.method().toUpperCase() + " " + url.value();
                    routes.put(key, new Route(instance, m));
                }
            }
        } catch (Exception e) {
            throw new ServletException("Échec init FrontController", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI().substring(req.getContextPath().length());
        String key = req.getMethod().toUpperCase() + " " + path;

        Route route = routes.get(key);
        if (route == null) {
            resp.sendError(404, "Aucune action pour : " + key);
            return;
        }

        try {
            Method method = route.method();
            Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                Parameter p = params[i];
                
                if (p.getType().equals(FileUpload.class)) {
                    args[i] = ParameterBinder.bindFileUpload(req, p);
                } else if (p.getType().equals(String.class)) {
                    args[i] = req.getParameter(p.getName());
                } else if (p.getType().equals(HttpServletRequest.class)) {
                    args[i] = req;
                } else if (p.getType().equals(HttpServletResponse.class)) {
                    args[i] = resp;
                } else if (p.getType().equals(Session.class)) {
                    args[i] = new Session(req);
                }
            }

            Object retour = method.invoke(route.controller(), args);

            if (retour instanceof String view && view.startsWith("/WEB-INF/")) {
                req.getRequestDispatcher(view).forward(req, resp);
            } else if (retour instanceof String redirect && redirect.startsWith("redirect:")) {
                resp.sendRedirect(req.getContextPath() + redirect.substring("redirect:".length()));
            }

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            cause.printStackTrace();
            resp.sendError(500, "Erreur dans le contrôleur : " + cause.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Erreur framework : " + e.toString());
        }
    }
}