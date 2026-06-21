
package com.sprit.core;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import vue.Utilitaires;

public class FrameworkServlet extends HttpServlet {

    private List<String> controllerClasses = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        String packageName = getServletConfig().getInitParameter("controllerPackage");
        if (packageName == null || packageName.isEmpty()) {
            System.out.println("[SPRIT] init: param 'controllerPackage' missing, skipping controller scan");
            return;
        }
        try {
            controllerClasses = Utilitaires.getControllerClasses(packageName);
            System.out.println("[SPRIT] Controllers trouvés : " + controllerClasses);
        } catch (Exception e) {
            throw new ServletException("Erreur lors du chargement des controllers", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Capture l'URL
        String uri = req.getRequestURI();
        String method = req.getMethod();

        // Affiche dans les logs
        System.out.println("[SPRIT Framework] Requête interceptée : " + method + " " + uri);

        // Réponse : affiche pour l'instant la liste des controllers trouvés
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>SPRIT Framework</title></head>");
        out.println("<body>");
        out.println("<h1> Controllers chargés :</h1>");
        out.println("<ul>");
        for (String c : controllerClasses) {
            out.println("<li>" + c + "</li>");
        }
        out.println("</ul>");
        out.println("<hr>");
        out.println("<p><i>SPRIT Framework</i></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
