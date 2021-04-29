<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Recuperar contraseña</title>
<link href="https://fonts.googleapis.com/css2?family=Patrick+Hand&family=Zen+Dots&display=swap" rel="stylesheet"> 
<link rel=stylesheet href=css/loginstyle.css type=text/css>
</head>
<body>

<img id="background" src="images/home_bg.png">

<div id="container">
	<div id="content">
		<p id="mensaje" class="label">Introduce tu correo electrónico para recibir un código de recuperación</p>
		<p id="label" class="label">Correo electrónico</p>
		<input type="text" id="campo" class="input" name="email" placeholder="correo@electroni.co" />
		<input type="hidden" id="phase" name="phase" value=""/>
		<button id="button" onclick="enviar()">Ok</button>
	</div>
</div>

<script>

	var phase = '<%=(String)request.getAttribute("phase")%>';
	var email = '<%=(String)request.getAttribute("email")%>';

	if (phase == "c")
	{
		document.getElementById("mensaje").innerHTML = "Si este correo pertenece a una cuenta de Rocket Ruckus, te habrá llegado un código de recuperación. Introdúcelo en la caja de debajo.";
		document.getElementById("label").innerHTML = "Código";
		document.getElementById("campo").name = "code";
		document.getElementById("campo").placeholder = "1312";
	}
	else if (phase == "np")
	{
		document.getElementById("mensaje").innerHTML = "Introduce la nueva contraseña";
		document.getElementById("label").innerHTML = "Nueva contraseña";
		document.getElementById("campo").name = "newpass";
		document.getElementById("campo").placeholder = "contraseñanueva123";
		document.getElementById("campo").type = "password";
	}
	else if (phase == "fin")
	{
		sessionStorage.setItem("cambiado","true");
		location.replace("login.jsp");
	}
	else
	{
		phase = "r";
	}
	document.getElementById("phase").value = phase;		
	
	function enviar()
	{
		var code = "a";
		var newpass = "a";
		if(phase == "r"){ email = document.getElementById("campo").value; }
		if(phase == "c"){ code = document.getElementById("campo").value; }
		if(phase == "np"){ newpass = document.getElementById("campo").value; }
		var destination = "recuperacion?phase=" + phase + "&email=" + email + "&code=" + code + "&newpass=" + newpass
		console.log(destination);
		location.replace(destination);
	}

</script>



</body>
</html>