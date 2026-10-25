package mg.itu.mvc.session;

import jakarta.servlet.http.HttpSession;

public class Session {
    private final HttpSession httpSession;

    public Session(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    // Ajouter ou modifier une variable de session
    public void set(String cle, Object valeur) {
        httpSession.setAttribute(cle, valeur);
    }

    // Récupérer une variable de session
    public Object get(String cle) {
        return httpSession.getAttribute(cle);
    }

    // Supprimer une variable
    public void remove(String cle) {
        httpSession.removeAttribute(cle);
    }

    // Détruire toute la session (déconnexion)
    public void invalidate() {
        httpSession.invalidate();
    }
}