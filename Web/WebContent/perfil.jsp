<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Perfil</title>
<link rel=stylesheet href=css/profilestyle.css type=text/css>
<link rel=stylesheet href=css/modalstyle.css type=text/css>
<link href="https://fonts.googleapis.com/css2?family=Patrick+Hand&family=Zen+Dots&display=swap" rel="stylesheet"> 
</head>
<body>

<img id="background" src="images/ingame_bg.png">

<div id="friendRequest" class="modal">

  <div class="modal-content">
    <span class="close" onclick="ocultar()">&times;</span>
    <div id="modal-text">
	    <p>¿Aceptas a</p>
	    <p id="newFriend"></p>
	    <p>como amigo/a?</p>
	    <button onclick="acceptFriend()">Aceptar</button>
	    <button onclick="declineFriend()">Rechazar</button>
    </div>
  </div>

</div>

<div id="confirmDelete" class="modal">

  <div class="modal-content">
    <span class="close" onclick="ocultar()">&times;</span>
    <div id="modal-text">
	    <p>Eliminar la cuenta es un proceso definitivo, todos tus logros y estadísticas desaparecerán.</p>
	    <p>¿De verdad quieres eliminar tu cuenta?</p>
	    <button onclick="location.replace('borrar_cuenta')">Eliminar</button>
    </div>
  </div>

</div>

<div id="upperbar">
	<button id="back_button" onclick="location.assign('buscar_partidas')">Volver a inicio</button>
	<p id="prof_title">Perfil</p>
</div>

<div id="container">
	<div id="prof" class="column">
		<div class="column_img_div">
			<img id="icon" src="images/icon_01_200px.png">
		</div>
		<div id="info">
			<div class="info_div">
				<p>Nombre de usuario</p>
				<input id="user_input" value="" type="text" placeholder=<%=(String)request.getSession().getAttribute("id_user")%> disabled>
				<button id="edit_user" class="edit_info" onclick="editUsername()">Editar</button>
				<button id="submit_user" class="submit_info" onclick="submitUsername()">Aceptar</button>
				<button id="cancel_user" class="submit_info" onclick="cancelUsername()">Cancelar</button>
			</div>
			<div class="info_div">
				<p>Correo electrónico</p>
				<input id="email_input" value="" type="text" placeholder=<%=(String)request.getSession().getAttribute("email")%> disabled>
				<button id="edit_email" class="edit_info" onclick="editEmail()">Editar</button>
				<button id="submit_email" class="submit_info" onclick="submitEmail()">Aceptar</button>
				<button id="cancel_email" class="submit_info" onclick="cancelUsername()">Cancelar</button>
			</div>
			<div class="info_div">
				<p>Cambio de contraseña</p>
				<input id="pass_input" value="" type="password" placeholder="Password" style="display:none">
				<button id="edit_pass" class="edit_info" onclick="editPass()">Editar</button>
			</div>
			<div id="pass_conf_div" class="info_div" style="display:none">
				<p>Confirma la nueva contraseña</p>
				<input id="pass_confirm" value="" type="password" placeholder="Password">
				<button id="submit_pass" class="submit_info" onclick="submitPass()">Aceptar</button>
				<button id="cancel_pass" class="submit_info" onclick="cancelPass()">Cancelar</button>
			</div>
			<div class="info_div">
				<p>Eliminar cuenta</p>
				<button id="delete_account" class="edit_info" onclick="deleteAcc()">Eliminar</button>
			</div>
		</div>
	</div>
	<div id="stats" class="column">
		<div class="column_img_div">
			<img id="friends_img" src="images/amigos.png">
		</div>
		<div id="partidas">
			<p id="games">Partidas jugadas: <%=request.getAttribute("pjugadas")%></p>
			<p id="wins">Partidas ganadas: <%=request.getAttribute("pganadas")%></p>
		</div>
		<div id="achievements">
		</div>
	</div>
	<div id="friends" class="column">
		<div class="column_img_div">
			<img id="friends_img" src="images/amigos.png">
		</div>
		<div id=newFriend_div>
			<input type="text" id="newFriend_input" placeholder="NUEVO AMIGO">
			<button onclick="addFriend()" id="addFriend_button">Enviar solicitud</button>
		</div>
		<div id="friend_list">
			<p id="solicitudes">¡Tienes nuevas solicitudes de amistad!</p>
			<p id="friend_placeholder">Vaya, aún no tienes ningún amigo</p>
		</div>
	</div>
</div>

</body>

<script>
		
	const friendList = document.getElementById("friend_list");
	const newRequest = document.getElementById("solicitudes");
	
	var friends = <%=(String)request.getAttribute("amigos")%>
	var num_friends = 0;
	
	var solicitudes = <%=(String)request.getAttribute("solicitudes")%>
	var num_solicitudes = 0;
	
	var requestFrom ;
	const friendRequest = document.getElementById("friendRequest");

	const confirmDelete = document.getElementById("confirmDelete");
	
	const addFriendInput = document.getElementById("newFriend_input")
	
	const achievements = document.getElementById("achievements")
	var logros = <%=(String)request.getAttribute("logros")%>
	
	for (var i=0;i<logros.length;i++){
		var entry = '';
		if(logros[i].num>=logros[i].completed){
			entry = '<div class="achievement">' +
						'<img class="ach_icon" src="images/achievement.png">' +
						logros[i].lname + ': ¡Completado!' +
						'</div>';
		} else {
			entry = '<div class="locked_achievement">' +
			'<img class="ach_icon" src="images/achievement_locked.png">' +
			logros[i].lname + ': ' + logros[i].num + '/' + logros[i].completed +
			'</div>';
		}
		achievements.innerHTML+=entry;
    }
	
	if(solicitudes.length>0){
    	newRequest.style.display="block";
    	for (var i=0;i<solicitudes.length;i++){
    		var entry = '<div class="friendRequest" onclick="showRequest(\''+solicitudes[i]+'\')">'+solicitudes[i]+' (Pendiente)</div>';
    		friendList.innerHTML+=entry;
        }
	}
	
	if(friends.length>0){
    	friend_placeholder.style.display="none";
        for (var i=0;i<friends.length;i++){
    		var entry = '<div class="friend">'+friends[i]+'<div class="deleteFriend" onclick="deleteFriend(\''+friends[i]+'\')">X</div></div>';
    		friendList.innerHTML+=entry;
    		num_friends++;
        }
    }
	
	if(friends.length>=30){
		var div = document.getElementById("newFriend_div");
		div.style.visibility='hidden';
	}
	
	function showRequest(newFriend){
		requestFrom = newFriend;
		const friend = document.getElementById("newFriend");
		friend.innerHTML=newFriend;
		friendRequest.style.display="block";
    	window.onclick = function(event) {
   		  if (event.target == friendRequest) {
   			friendRequest.style.display = "none";
   		  }
   		}
	}
	
	function deleteAcc(){
		confirmDelete.style.display="block";
    	window.onclick = function(event) {
   		  if (event.target == confirmDelete) {
   			confirmDelete.style.display = "none";
   		  }
   		}
	}
	
	function acceptFriend(){
		window.location.replace("aceptar_solicitud?amigo="+requestFrom);
	}
	
	function declineFriend(){
		window.location.replace("rechazar_solicitud?amigo="+requestFrom);
	}
	
	function addFriend() {
		var newFriend = addFriendInput.value;
		window.location.replace("enviar_solicitud?amigo="+newFriend)
	}
	
	function deleteFriend(friend) {
		window.location.replace("borrar_amigo?amigo="+friend);
	}
	
	function ocultar()
    {
    	friendRequest.style.display="none";
    }
	
	function editUsername(){
		document.getElementById("user_input").disabled=false;
		document.getElementById("submit_user").style.display="inline-block";
		document.getElementById("cancel_user").style.display="inline-block";
		document.getElementById("edit_user").style.display="none";
	}
	
	function editEmail(){
		document.getElementById("email_input").disabled=false;
		document.getElementById("submit_email").style.display="inline-block";
		document.getElementById("cancel_email").style.display="inline-block";
		document.getElementById("edit_email").style.display="none";
	}
	
	function editPass(){
		document.getElementById("pass_input").style.display="inline-block";
		document.getElementById("pass_conf_div").style.display="block";
		document.getElementById("submit_pass").style.display="inline-block";
		document.getElementById("cancel_pass").style.display="inline-block";
		document.getElementById("edit_pass").style.display="none";
	}
	
	function cancelUsername(){
		document.getElementById("user_input").disabled=true;
		document.getElementById("user_input").value="";
		document.getElementById("submit_user").style.display="none";
		document.getElementById("cancel_user").style.display="none";
		document.getElementById("edit_user").style.display="inline-block";
	}
	
	function cancelEmail(){
		document.getElementById("email_input").disabled=true;
		document.getElementById("email_input").value="";
		document.getElementById("submit_email").style.display="none";
		document.getElementById("cancel_email").style.display="none";
		document.getElementById("edit_email").style.display="inline-block";
	}
	
	function cancelPass(){
		document.getElementById("pass_input").style.display="none";
		document.getElementById("pass_conf_div").style.display="none";
		document.getElementById("pass_input").value="";
		document.getElementById("pass_confirm").value="";
		document.getElementById("submit_pass").style.display="none";
		document.getElementById("cancel_pass").style.display="none";
		document.getElementById("edit_pass").style.display="inline-block";
	}
	
	function submitUsername(){
		var newInfo = document.getElementById("user_input").value;
		location.replace("cambiar_nombre?name="+newInfo);
	}
	
	function submitEmail(){
		var newInfo = document.getElementById("email_input").value;
		location.replace("cambiar_correo?email="+newInfo);
	}
	
	function submitPass(){
		var newInfo = document.getElementById("pass_input").value;
		var confirm = document.getElementById("pass_confirm").value
		location.replace("cambiar_password?pass="+newInfo+"&cpass="+confirm);
	}
		
</script>

</html>