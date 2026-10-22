<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html><html><head><meta charset="UTF-8"><title>Legacy MFA Demo Login</title></head>
<body><h1>Legacy MFA Demo</h1>
<form method="post" action="${pageContext.request.contextPath}/login/password">
  <label>Username <input name="username" autocomplete="username" required></label>
  <label>Password <input name="password" type="password" autocomplete="current-password" required></label>
  <button type="submit">Continue</button>
</form>
<p>Demo-only. Do not enter real credentials.</p>
</body></html>
