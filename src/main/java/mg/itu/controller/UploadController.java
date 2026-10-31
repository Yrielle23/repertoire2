package mg.itu.controller;

import mg.itu.mvc.annotation.Controller;
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

    @Url(value = "/session", method = "GET")
    public String sessionPage() {
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