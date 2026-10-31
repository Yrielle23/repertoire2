package framework;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import framework.annotation.ApiController;

@WebServlet("/*")
public class FrontController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        handleRequest(req, res, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        handleRequest(req, res, "POST");
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse res, String method) 
            throws ServletException, IOException {
        
        // Récupère l'URL après le contexte
        String url = req.getRequestURI().substring(req.getContextPath().length());
        if (url.isEmpty()) url = "/";

        System.out.println("[FrontController] " + method + " " + url);

        // Cherche le mapping
        Map<String, Mapping> routes = ("GET".equals(method)) 
            ? (Map<String, Mapping>) getServletContext().getAttribute("routesGet")
            : (Map<String, Mapping>) getServletContext().getAttribute("routesPost");

        if (routes == null || !routes.containsKey(url)) {
            res.setContentType("application/json; charset=UTF-8");
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("{\"status\":\"error\",\"message\":\"Route not found: " + method + " " + url + "\"}");
            return;
        }

        try {
            Mapping mapping = routes.get(url);
            Class<?> controllerClass = Class.forName(mapping.getClassName());

            if (!controllerClass.isAnnotationPresent(ApiController.class)) {
                res.setContentType("application/json; charset=UTF-8");
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("{\"status\":\"error\",\"message\":\"Controller non autorisé: "
                        + controllerClass.getName() + " n'est pas annoté @ApiController\"}");
                return;
            }

            Object controller = controllerClass.getDeclaredConstructor().newInstance();

            Method actionMethod = controllerClass.getDeclaredMethod(mapping.getMethodName());
            Object result = actionMethod.invoke(controller);

            // Réponse JSON
            res.setContentType("application/json; charset=UTF-8");
            res.getWriter().write((String) result);

        } catch (Exception e) {
            System.err.println("[FrontController] Error: " + e.getMessage());
            e.printStackTrace();
            res.setContentType("application/json; charset=UTF-8");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}