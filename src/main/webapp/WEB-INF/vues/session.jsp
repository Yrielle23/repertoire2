<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Session</title>
</head>
<body>
    <h1>Gestion de session</h1>

    <%
        String utilisateur = (String) session.getAttribute("utilisateur");
        if (utilisateur != null && !utilisateur.isBlank()) {
    %>
        <p>Utilisateur connecté : <strong><%= utilisateur %></strong></p>
        <form method="post" action="${pageContext.request.contextPath}/session/logout">
            <button type="submit">Se déconnecter</button>
        </form>
    <%
        } else {
    %>
        <form method="post" action="${pageContext.request.contextPath}/session/login">
            <label for="nom">Nom :</label>
            <input type="text" id="nom" name="nom" required>
            <button type="submit">Se connecter</button>
        </form>
    <%
        }
    %>

    <p>
        <a href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
    </p>
</body>
</html>
