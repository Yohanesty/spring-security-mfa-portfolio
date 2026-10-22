<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html><html><head><meta charset="UTF-8"><title>OTP Challenge</title></head>
<body><h1>Second factor</h1>
<form method="post" action="${pageContext.request.contextPath}/mfa/verify">
  <label>Six-digit code <input name="otp" inputmode="numeric" pattern="[0-9]{6}" autocomplete="one-time-code" required></label>
  <button type="submit">Verify</button>
</form></body></html>
