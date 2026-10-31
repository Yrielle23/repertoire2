package mg.itu.controller;

import mg.itu.mvc.annotation.Controller;
import mg.itu.mvc.annotation.Role;
import mg.itu.mvc.annotation.Url;
import mg.itu.mvc.file.FileUpload;
import mg.itu.mvc.session.Session;

@Controller
public class UploadController {

    @Url(value = "/", method = "GET")
    public String index() {
        return "/WEB-INF/vues/index.jsp";
    }

    @Url(value = "/upload", method = "POST")
    public String saluer(FileUpload filemap) {
        if (filemap == null || filemap.getContent() == null) {
            return "/WEB-INF/vues/resultat.jsp";
        }
        return "/WEB-INF/vues/resultat.jsp";
    }

    @Url(value = "/session-set", method = "GET")
    public String setSession(Session session) {
        session.set("utilisateur", "Yrielle");
        return "/WEB-INF/vues/resultat.jsp";
    }

    @Url(value = "/session-get", method = "GET")
    public String getSession(Session session) {
        return "/WEB-INF/vues/resultat.jsp";
    }

    @Url(value = "/admin", method = "GET")
    public String pageAdmin(Session session) {
        return "/WEB-INF/vues/resultat.jsp";
    }

    @Url(value = "/logout", method = "GET")
    public String logout(Session session) {
        session.disconnect();
        return "redirect:/session";
    }

    @Url(value = "/login-admin", method = "GET")
    public String loginAdmin(Session session) {
        session.connect("admin");
        return "redirect:/session";
    }

    @Url(value = "/login-user", method = "GET")
    public String loginUser(Session session) {
        session.connect("user");
        return "redirect:/session";
    }

    @Url(value = "/admin-only", method = "GET")
    @Role("ADMIN")
    public String adminOnly(Session session) {
        return "/WEB-INF/vues/resultat.jsp";
    }

    @Url(value = "/user-space", method = "GET")
    @Role({"USER", "ADMIN"})
    public String userSpace(Session session) {
        return "/WEB-INF/vues/resultat.jsp";
    }

    @Url(value = "/session", method = "GET")
    public String afficherSession() {
        return "/WEB-INF/vues/session.jsp";
    }

    @Url(value = "/session/login", method = "POST")
    public String connecter(String nom, Session session) {
        if (nom == null || nom.isBlank()) {
            return "/WEB-INF/vues/session.jsp";
        }
        session.connect(nom);
        return "redirect:/session";
    }

    @Url(value = "/session/logout", method = "POST")
    public String deconnecter(Session session) {
        session.disconnect();
        return "redirect:/session";
    }
}