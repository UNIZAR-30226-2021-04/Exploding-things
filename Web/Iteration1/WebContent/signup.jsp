<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Sign Up</title>
</head>
<body>

<% String error = (String)request.getAttribute("error"); if (error!=null && !error.isEmpty()) out.print(error+"<br>"); %>

<form action="signup" method="post">
<label for="username">Nombre de usuario</label><br>
<input type="text" id="username" name="user" placeholder="Nombre"><br>
<label for="Email">Correo electrónico</label><br>
<input type="text" id="email" name="mail" placeholder="Email"><br>
<label for="password">Contraseña</label><br>
<input type="password" id="password" name="pass" placeholder="Contraseña"><br>
<label for="confirmpassword">Confirmar contraseña</label><br>
<input type="password" id="confirmpassword" name="cpass" placeholder="Contraseña"><br>
<button type="submit">Register</button>
</form>

</body>
</html>