package com.sprit.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vue.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;

public class DeptController {

    @RequestMapping(value = "/dept/new", method = "GET")
    public void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>DeptController</title></head>");
        out.println("<body>");
        out.println("<h1>Route trouvée</h1>");
        out.println("<p>URL : " + req.getRequestURI() + "</p>");
        out.println("<p>Controller : DeptController</p>");
        out.println("<p>Méthode : create</p>");
        out.println("<hr>");
        out.println("<p>Cette page montre l'URL, le contrôleur et la méthode associés.</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
