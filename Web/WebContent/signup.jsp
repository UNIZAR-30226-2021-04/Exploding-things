<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Sign Up</title>
<link href="https://fonts.googleapis.com/css2?family=Patrick+Hand&family=Zen+Dots&display=swap" rel="stylesheet"> 
<link rel=stylesheet href=css/loginstyle.css type=text/css>
</head>
<body>

<% String error = (String)request.getAttribute("error"); if (error!=null && !error.isEmpty()) out.print(error+"<br>"); %>

<img id="background" src="images/home_bg.png">

<div id="container">
	<div id="content">
		<form action="signup" method="post">
			<label class="label" for="username">Nombre de usuario</label><br>
			<input type="text" class="input" name="user" placeholder="Nombre"><br>
			<label class="label" for="Email">Correo electrónico</label><br>
			<input type="text" class="input" name="mail" placeholder="Email"><br>
			<label class="label" for="password">Contraseña</label><br>
			<input type="password" class="input" name="pass" placeholder="Contraseña"><br>
			<label class="label" for="confirmpassword">Repetir contraseña</label><br>
			<input type="password" class="input" name="cpass" placeholder="Contraseña"><br>
			<button id="button" type="submit">Registrarse</button>
		</form>
	</div>
</div>

</body>
</html>