package mg.itu.mvc.session;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private final HttpSession httpSession;
    
    private final Map<String, Object> localMap = new HashMap<>();
    
    private boolean modified = false;

    public Session(HttpSession httpSession) {
        this.httpSession = httpSession;
        
        var names = httpSession.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            localMap.put(name, httpSession.getAttribute(name));
        }
    }

    // Ajouter ou modifier une variable 
    public void set(String cle, Object valeur) {
        localMap.put(cle, valeur);
        modified = true;
    }

    public Object get(String cle) {
        return localMap.get(cle);
    }

    // Supprimer une variable (dans la map locale)
    public void remove(String cle) {
        if (localMap.containsKey(cle)) {
            localMap.remove(cle);
            modified = true;
        }
    }

    public void invalidate() {
        localMap.clear();
        httpSession.invalidate();
        modified = false;
    }

    public void commit() {
        if (!modified) return;
        for (Map.Entry<String, Object> entry : localMap.entrySet()) {
            httpSession.setAttribute(entry.getKey(), entry.getValue());
        }
        modified = false; 
    }
}