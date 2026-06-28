package com.sprit.core;

import com.sprit.controller.DeptController;
import com.sprit.routing.MethodeInfo;
import com.sprit.routing.UrlMethode;
import vue.RequestMapping;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrameworkServlet extends HttpServlet {

    private static final Map<UrlMethode, MethodeInfo> ROUTE_MAP = new LinkedHashMap<>();

    static {
        registerController(new DeptController());
    }

    private static void registerController(Object controller) {
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
        String uri = req.getRequestURI();
        String method = req.getMethod();
        UrlMethode lookup = new UrlMethode(uri, method);

        System.out.println("[SPRIT Framework] Requête interceptée : " + method + " " + uri);

        MethodeInfo handler = ROUTE_MAP.get(lookup);
        if (handler != null) {
            try {
                handler.invoke(req, resp);
                return;
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Erreur interne du framework : " + e.getMessage());
                return;
            }
        }

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
