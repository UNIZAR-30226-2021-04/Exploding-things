<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="servlets_frontend.Rck_conn" %>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Home</title>
<link href="https://fonts.googleapis.com/css2?family=Patrick+Hand&family=Zen+Dots&display=swap" rel="stylesheet"> 
<link rel=stylesheet href=css/homestyle.css type=text/css>
<link rel=stylesheet href=css/modalstyle.css type=text/css>
</head>

<body onpageshow="pending()">

<% String id_user = (String)request.getSession().getAttribute("id_user"); %>
<% String error=(String)request.getAttribute("error"); %>

<div class="error"><% if (error!=null){out.print(error);} %></div>

<audio autoplay loop>
	<source src="music/home_music.mp3" type="audio/mpeg">
</audio>

<img id="background" src="images/home_bg.png">

<div id="passBox" class="modal">

  <div class="modal-content">
    <span class="close" onclick="ocultar()">&times;</span>
    <div id="modal-form">
	    <p>Introduce la contraseña:</p>
	    <input type="text" id="passInput" value="" placeholder="1234">
	    <button id="submitPassButton">Entrar</button>
    </div>
  </div>

</div>

<div id="mailBox" class="modal">

  <div class="modal-content">
    <span class="close" onclick="ocultarMail()">&times;</span>
    <div id="modal-form">
	    <p>Aquí van las invitaciones a partida:</p>
	    <p id="mailbox_placeholder">No tienes invitaciones a partida pendientes</p>
	    <div class="lobby_list" id="mail_list">
		
		</div>
    </div>
  </div>

</div>

<div id=upperbar>
	<img onclick="location.replace('cargarPerfil')"id="playerPortrait" src="images/icon_01_200px.png">
	<p id="playerName"><%=id_user%></p>
	<div style="flex-grow:2"></div>
	<button id="mailButton" onclick="mostrarMail()">Buzón</button>
	<button id="searchButton" onclick="buscar()">Buscar partidas</button>
	<button id="logoutButton" onclick="logout()">Cerrar sesión</button>
</div>

<div id=mainscreen>
	<div class="game_div" id="pub_lobbys">
		<img class="lobby_img" src="images/public_lobby.png" alt="Partida pública: Todo el mundo puede unirse">
		<button id="createPublic" class="createButton" onclick="crearPublica()">Crear partida pública</button>
		<p id="pub_lobby_placeholder">No se han encontrado partidas publicas</p>
		<div class="lobby_list" id="pub_lobby_list">
		
		</div>
		
	</div>
	<div class="game_div" id="priv_lobbys" >
		<img class="lobby_img" src="images/private_lobby.png" alt="Partida privada: Salas con contraseña">
		<button id="createPrivate" class="createButton" onclick="mostrar(),asignarAPass('crear')">Crear partida privada</button>
		<p id="priv_lobby_placeholder">No se han encontrado partidas privadas</p>
		<div class="lobby_list" id="priv_lobby_list">
		
		</div>
	</div>
</div>

</body>

<script>

    const pubLobbyList = document.getElementById("pub_lobby_list");
    const privLobbyList = document.getElementById("priv_lobby_list");
    const pub_placeholder = document.getElementById("pub_lobby_placeholder");
    const priv_placeholder = document.getElementById("priv_lobby_placeholder");
    const mail_placeholder = document.getElementById("mailbox_placeholder");
    const mailList = document.getElementById("mail_list");
    
    const passbox = document.getElementById("passBox");
    const passButton = document.getElementById("submitPassButton")
    const passInput = document.getElementById("passInput")
    const mailbox = document.getElementById("mailBox");
    
    var lobbys = <%=(String)request.getAttribute("lobby_list")%>;
    var user = '<%=(String)request.getSession().getAttribute("id_user")%>';
    var mails = [];
    
    var uniendose=false;
    
    //Parámetros utilizados para portabilidad
    var id_lobby = <%=(Integer)request.getAttribute("id_lobby")%>
    var muerte = <%=(Integer)request.getAttribute("muerte")%>
    var pstart = <%=(Integer)request.getAttribute("pstart")%>
    
    if (id_lobby>0 && pstart==0){
    	const socket = new WebSocket("<%=Rck_conn.LOBBYWS%>");
    	var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:String(id_lobby) , portabilidad:true};
    	socket.binaryType = "arraybuffer";
    	socket.onopen = function (event) {
	    	socket.send(JSON.stringify(exitJSON));
	    	socket.close();
	    };
    } else if (id_lobby>0 && pstart==1){
    	if(muerte==0) {
    		sessionStorage.setItem("id_user",user);
    		sessionStorage.setItem("id_lobby",id_lobby);
    		location.assign("partida.jsp");
    	} else if(muerte==1){
	    	const socket = new WebSocket("<%=Rck_conn.PARTIDAWS%>");
	    	var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:String(id_lobby) , portabilidad:true};
	    	socket.binaryType = "arraybuffer";
	    	socket.onopen = function (event) {
		    	socket.send(JSON.stringify(exitJSON));
		    	socket.close();
		    };
    	}
    }
    
    //Si tenemos lobbys y n hay ningún mensaje pendiente de envío
    if(lobbys.length>0 && !sessionStorage.getItem("pending")){
    	pubLobbyList.innerHTML="";
    	privLobbyList.innerHTML="";
    	var pubs=1;
    	var privs=1;
        for (var i=0;i<lobbys.length;i++){
        	var lobby_i = lobbys[i];
        	if(lobbys[i].pass==0){
            	pub_placeholder.style.display="none";
	    		var entry = '<button type="submit" class="lobbybutton" onclick="unirse('+lobbys[i].id_lobby+')">'+'Sala pública ' + pubs + ':   ID-'+lobbys[i].id_lobby + " - "+lobby_i.nuser + "/4 usuarios" + '</button>';
	    		pubLobbyList.innerHTML+=entry;
	    		pubs++;
        	} else {
            	priv_placeholder.style.display="none";
        		var entry = '<button type="submit" class="lobbybutton" onclick="mostrar(),asignarAPass(\'unirse\','+lobbys[i].id_lobby+')">'+'Sala privada ' + privs + ':   ID-'+lobbys[i].id_lobby + " - "+lobby_i.nuser + "/4 usuarios" + '</button>';							
	    		privLobbyList.innerHTML+=entry;
	    		privs++;
        	}
        }
    }
    
    const friend_socket = new WebSocket("<%=Rck_conn.INVITARWS%>");
    friend_socket.binaryType = "arraybuffer";
    
    friend_socket.onopen = function (event) {
    	var joinJSON = { msg_type: 'unir', id_user:user };
    	friend_socket.send(JSON.stringify(joinJSON));
    }
    
    friend_socket.onmessage = function (event) {
    	var msg = JSON.parse(event.data);
    	console.log(msg);
    	mails.push(msg);
    }
    
    function buscar()
    {
    	var exitJSON = { msg_type: 'salir', id_user:user };
    	friend_socket.send(JSON.stringify(exitJSON));
    	document.getElementById("searchButton").disabled = true;
    	sessionStorage.removeItem("salimal");
    	location.replace("buscar_partidas");
    }
    
    //On click del botón "crearPartida". Desactiva el botón y cancela el flag de mal retorno a partida
    function crearPublica()
    {
    	if(!uniendose){
    		var exitJSON = { msg_type: 'salir', id_user:user };
    		friend_socket.send(JSON.stringify(exitJSON));
    		uniendose=true;
	    	document.getElementById("createPublic").disabled = true;
	    	sessionStorage.removeItem("salimal");
	    	location.assign("crear_partida?id_user="+user);
    	}
    }
    
    function crearPriv(pass){
    	if(!uniendose){
    		var exitJSON = { msg_type: 'salir', id_user:user };
    		friend_socket.send(JSON.stringify(exitJSON));
    		uniendose=true;
	    	document.getElementById("createPrivate").disabled = true;
	    	sessionStorage.removeItem("salimal");
	    	location.assign("crear_partida?id_user="+user+"&pass="+pass);
    	}
    }
    
  	//On click de los botones de lobby público. Desactiva todos los botones y cancela el flag de mal retorno a partida
    function unirse(idlobby)
    {
        if(!uniendose){
    		var exitJSON = { msg_type: 'salir', id_user:user };
    		friend_socket.send(JSON.stringify(exitJSON));
        	uniendose=true;
        	sessionStorage.removeItem("salimal");
        	location.assign("unirse_lobby?id_user="+user+"&id_lobby="+idlobby);
        }
    }
  	
  	function unirsePriv(idlobby,pass){
  		if(!uniendose){
    		var exitJSON = { msg_type: 'salir', id_user:user };
    		friend_socket.send(JSON.stringify(exitJSON));
        	uniendose=true;
        	sessionStorage.removeItem("salimal");
        	location.assign("unirse_lobby?id_user="+user+"&id_lobby="+idlobby+"&pass="+pass);
        }
  	}
    
    function mostrar()
    {
    	passbox.style.display="block";
    	window.onclick = function(event) {
   		  if (event.target == passbox) {
   		    passbox.style.display = "none";
   		  }
   		}
    }
    
    function mostrarMail()
    {
    	mailbox.style.display="block";
    	window.onclick = function(event) {
   		  if (event.target == mailbox) {
   		    mailbox.style.display = "none";
   		  }
   		}
    	if (mails.length>0)
    	{
           	mail_placeholder.style.display="none";
        	mailList.innerHTML="";
        	var invitations=1;
            for (var i=0;i<mails.length;i++){
            	var mail_i = mails[i];
               	var friend = mail_i.id_user;
        		var lobby = mail_i.id_lobby;
        		var pass = mail_i.pass;
        		var texto;
        		var onclick;
        		if(pass=="null"){
        			texto = 'INVITACIÓN DE: '+friend+' - PARTIDA PÚBLICA'
        			onclick = '"unirse('+lobby+')"'
        		} else {
        			texto = 'INVITACIÓN DE: '+friend+' - PARTIDA PRIVADA'
        			onclick = '"unirsePriv('+lobby+',\''+pass+'\')"'
        		}
   	    		var entry = '<button type="submit" ' +
   	    					' onclick='+onclick+' class="lobbybutton">'+texto+'</button>';
   	    		mailList.innerHTML+=entry;
   	    		invitations++;
    		}
            console.log(mailList);
    	}
    }
    
    function ocultar()
    {
    	passbox.style.display="none";
    }
    
    function ocultarMail()
    {
    	mailbox.style.display="none";
    }
    
    function asignarAPass(target,idlobby){
    	if(target=='crear'){
    		passButton.onclick= function(event) {
    			var lobby_pass = passInput.value;
    			crearPriv(lobby_pass);
    		}
    	} else if(target=='unirse'){
    		passButton.onclick= function(event) {
    			var lobby_pass = passInput.value;
    			unirsePriv(idlobby,lobby_pass);
    		}
    	}
    }
    
    function pending()
    {
    	if(sessionStorage.getItem("pending")){
    		var exitJSON = { msg_type: 'salir', id_user:user };
    		friend_socket.send(JSON.stringify(exitJSON));
    		var newloc = sessionStorage.getItem("pending");
    		sessionStorage.removeItem("pending");
    		location.replace(newloc);
    	}
    	
    	if(sessionStorage.getItem("notsent")){
    		var exitJSON=sessionStorage.getItem("notsent");
    		const socket = new WebSocket("<%=Rck_conn.PARTIDAWS%>");
    	    socket.binaryType = "arraybuffer";
    	    socket.onopen = function (event) {
    	    	socket.send(exitJSON);
    	    	socket.close();
        	    sessionStorage.removeItem("notsent");
    	    };
    	}
    	
    	if(sessionStorage.getItem("notsent_lobby")){
    		var exitJSON=sessionStorage.getItem("notsent_lobby");
    		const socket = new WebSocket("<%=Rck_conn.LOBBYWS%>");
    	    socket.binaryType = "arraybuffer";
    	    socket.onopen = function (event) {
    	    	socket.send(exitJSON);
    	    	socket.close();
        	    sessionStorage.removeItem("notsent_lobby");
    	    };
    	}
    	
    	document.getElementById("searchButton").disabled = false;
    	document.getElementById("createPublic").disabled = false;
    	document.getElementById("createPrivate").disabled = false;
    	
    }
    
    function logout() {
    	var exitJSON = { msg_type: 'salir', id_user:user };
		friend_socket.send(JSON.stringify(exitJSON));
		window.location.assign('logout');
    }
    
</script>

</html>