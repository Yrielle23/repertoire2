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
    "/session-get"
}, loadOnStartup = 1)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,       // 1 Mo
    maxFileSize       = 20L * 1024 * 1024, // 20 Mo
    maxRequestSize    = 25L * 1024 * 1024  // 25 Mo
)
public class FrontController extends HttpServlet {

    private final Map<String, Route> routes = new HashMap<>();
    

    private final Map<Class<?>, Object> controllersInstances = new HashMap<>();

    private record Route(Object controller, Method method) {}

    @Override
    public void init() throws ServletException {
        try {

            List<Class<?>> controllers = List.of(
                Class.forName("mg.itu.controller.UploadController")
            );

            for (Class<?> c : controllers) {
                if (!c.isAnnotationPresent(Controller.class)) continue;

                Object instance;
                if (controllersInstances.containsKey(c)) {
                    instance = controllersInstances.get(c);
                } else {
                    instance = c.getDeclaredConstructor().newInstance();
                    controllersInstances.put(c, instance);
                    System.out.println("Singleton créé : " + c.getSimpleName());
                }

                for (Method m : c.getDeclaredMethods()) {
                    Url url = m.getAnnotation(Url.class);
                    if (url == null) continue;
                    String key = url.method().toUpperCase() + " " + url.value();
                    routes.put(key, new Route(instance, m));
                    System.out.println("Route enregistrée : " + key);
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

        Session sessionObj = null; 

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
                    sessionObj = new Session(req.getSession());
                    args[i] = sessionObj;
                }
            }

            // Appel de la méthode du contrôleur
            Object retour = method.invoke(route.controller(), args);

            if (sessionObj != null) {
                sessionObj.commit();
            }

            if (retour instanceof String view && view.startsWith("/WEB-INF/")) {
                req.getRequestDispatcher(view).forward(req, resp);
            } else if (retour instanceof String redirect && redirect.startsWith("redirect:")) {
                resp.sendRedirect(req.getContextPath() + redirect.substring("redirect:".length()));
            }

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            cause.printStackTrace();
            
            if (sessionObj != null) sessionObj.commit(); 
            
            resp.setContentType("text/plain;charset=UTF-8");
            resp.sendError(500, "Erreur dans le contrôleur : " + cause.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setContentType("text/plain;charset=UTF-8");
            resp.sendError(500, "Erreur framework : " + e.toString());
        }
    }
}