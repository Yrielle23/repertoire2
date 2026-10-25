package mg.itu.controller;

import mg.itu.mvc.annotation.Controller;
import mg.itu.mvc.annotation.Url;
import mg.itu.mvc.file.FileUpload;

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
}