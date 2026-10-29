package mg.itu.controller;

import mg.itu.mvc.annotation.AuthRequired;
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
            System.out.println("Aucun fichier reçu.");
            return "/WEB-INF/vues/resultat.jsp";
        }
        
        System.out.println("Reçu: " + filemap.getFileName()
                + " (" + filemap.getContent().length + " octets, ."
                + filemap.getExtension() + ")");
                
        return "/WEB-INF/vues/resultat.jsp";
    }

    // --- SPRINT 9 : Tests de Session ---

    @Url(value = "/session-set", method = "GET")
    public String setSession(Session session) {
        session.set("utilisateur", "Yrielle");
        System.out.println(">>> TEST SET : Session créée pour " + session.get("utilisateur") + " <<<");
        return "/WEB-INF/vues/resultat.jsp";
    }

    @Url(value = "/session-get", method = "GET")
    public String getSession(Session session) {
        Object user = session.get("utilisateur");
        System.out.println(">>> TEST GET : L'utilisateur récupéré est " + user + " <<<");
        return "/WEB-INF/vues/resultat.jsp";
    }

    // --- SPRINT 10 : Tests d'Authentification ---

    @Url(value = "/admin", method = "GET")
    @AuthRequired // ⭐ Cette méthode nécessite d'être connecté
    public String pageAdmin(Session session) {
        System.out.println(">>> Accès autorisé à la page admin pour : " + session.get("utilisateur") + " <<<");
        return "/WEB-INF/vues/resultat.jsp";
    }

    // ⭐ Route bonus pour tester la déconnexion
    @Url(value = "/logout", method = "GET")
    public String logout(Session session) {
        session.invalidate(); // Détruit la session
        System.out.println(">>> TEST LOGOUT : Session détruite <<<");
        return "/WEB-INF/vues/resultat.jsp";
    }
}