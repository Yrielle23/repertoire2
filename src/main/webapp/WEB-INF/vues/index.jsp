<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Upload</title></head>
<body>
  <h1>Envoyer un fichier</h1>
  <form method="post" action="${pageContext.request.contextPath}/upload"
        enctype="multipart/form-data">
      <input type="file" name="filemap" required>
      <button type="submit">Envoyer</button>
  </form>
</body>
</html>