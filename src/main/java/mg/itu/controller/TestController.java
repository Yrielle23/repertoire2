package mg.itu.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.mvc.annotation.Controller;
import mg.itu.mvc.annotation.Url;
import mg.itu.mvc.session.Session;

@Controller
public class TestController {

    @Url(value = "/session", method = "GET")
    public String afficherSession(HttpServletRequest request) {
        return "/WEB-INF/vues/session.jsp";
    }

    @Url(value = "/session/login", method = "POST")
    public String connecter(String nom, HttpServletRequest request) {
        if (nom == null || nom.isBlank()) {
            return "/WEB-INF/vues/session.jsp";
        }
        new Session(request).connect(nom);
        return "redirect:/session";
    }

    @Url(value = "/session/logout", method = "POST")
    public String deconnecter(HttpServletRequest request) {
        new Session(request).disconnect();
        return "redirect:/session";
    }
}