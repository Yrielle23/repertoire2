
package com.sprit.core;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FrameworkServlet extends HttpServlet {<ll
       @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        // Capture l'URL
        String uri = req.getRequestURI();
        String method = req.getMethod();
        
        // Affiche dans les logs
        System.out.println("[SPRIT Framework] Requête interceptée : " + method + " " + uri);
        
        // Réponse simple
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>SPRIT Framework</title></head>");
        out.println("<body>");
        out.println("<h1> SPRIT Framework a intercepté cette requête</h1>");
        out.println("<p>URL : " + uri + "</p>");
        out.println("<p>Méthode : " + method + "</p>");
        out.println("<hr>");
        out.println("<p><i>SPRIT Framework</i></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
