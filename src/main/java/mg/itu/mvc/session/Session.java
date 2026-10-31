package mg.itu.mvc.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.*;

public class Session {
    private final HttpSession httpSession;
    private final Map<String, Object> localMap = new HashMap<>();
    private boolean modified = false;

    private static final String USER_ATTRIBUTE = "utilisateur";

    public Session(HttpSession httpSession) {
        this.httpSession = httpSession;
        var names = httpSession.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            localMap.put(name, httpSession.getAttribute(name));
        }
    }

    public void connect(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        
        localMap.put(USER_ATTRIBUTE, userName.trim());
        modified = true;
    }

    public void disconnect() {
        localMap.clear();
        httpSession.invalidate();
        modified = false;
    }

    public String getUser() {
        Object user = localMap.get(USER_ATTRIBUTE);
        return user instanceof String ? (String) user : null;
    }

    public void set(String cle, Object valeur) {
        localMap.put(cle, valeur);
        modified = true;
    }

    public Object get(String cle) {
        return localMap.get(cle);
    }

    public void invalidate() {
        disconnect();
    }

    public void commit() {
        if (!modified) return;
        for (Map.Entry<String, Object> entry : localMap.entrySet()) {
            httpSession.setAttribute(entry.getKey(), entry.getValue());
        }
        modified = false;
    }

    public Set<String> getRoles(ServletContext context) {
        Set<String> result = new LinkedHashSet<>();
        String userName = getUser();
        if (userName == null || context == null) {
            return result;
        }

        String userRoles = context.getInitParameter("user-roles");
        if (userRoles == null || userRoles.isBlank()) {
            return result;
        }

        String normalizedUser = userName.trim().toLowerCase(Locale.ROOT);
        for (String mapping : userRoles.split("[;,]")) {
            if (mapping == null || !mapping.contains("=")) {
                continue;
            }
            String[] parts = mapping.split("=", 2);
            if (parts.length == 2 && parts[0].trim().toLowerCase(Locale.ROOT).equals(normalizedUser)) {
                String role = parts[1].trim().toUpperCase(Locale.ROOT);
                if (!role.isEmpty()) {
                    result.add(role);
                }
            }
        }
        return result;
    }

    public static Set<String> getConfiguredRoles(ServletContext context) {
        Set<String> roles = new LinkedHashSet<>();
        String configuredRoles = context == null ? null : context.getInitParameter("roles");
        if (configuredRoles == null || configuredRoles.isBlank()) {
            return roles;
        }
        for (String role : configuredRoles.split("[,;]")) {
            if (role != null && !role.isBlank()) {
                roles.add(role.trim().toUpperCase(Locale.ROOT));
            }
        }
        return roles;
    }

    public boolean hasRole(ServletContext context, String role) {
        return role != null && getRoles(context).contains(role.trim().toUpperCase(Locale.ROOT));
    }

    public boolean hasAnyRole(ServletContext context, String... requiredRoles) {
        if (requiredRoles == null || requiredRoles.length == 0) {
            return true;
        }
        Set<String> roles = getRoles(context);
        return Arrays.stream(requiredRoles)
                .filter(role -> role != null && !role.isBlank())
                .map(role -> role.trim().toUpperCase(Locale.ROOT))
                .anyMatch(roles::contains);
    }
}