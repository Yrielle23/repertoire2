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
}