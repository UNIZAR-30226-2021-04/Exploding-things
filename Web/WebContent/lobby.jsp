<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="servlets_frontend.Rck_conn" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Lobby</title>
<link href="https://fonts.googleapis.com/css2?family=Patrick+Hand&family=Zen+Dots&display=swap" rel="stylesheet"> 
<link rel=stylesheet href=css/lobbystyle.css type=text/css>
<link rel=stylesheet href=css/modalstyle.css type=text/css>
</head>
<body onbeforeunload= "return miranda()" onunload="return fancyExit()">

<%String user = (String)request.getAttribute("id_user");%>
<%String id_lobby =(String)request.getAttribute("id_lobby");%>
<%String am_i_host = (String)request.getAttribute("host");%>
<%String pass = (String)request.getAttribute("pass");%>
<%String foto = (String)request.getSession().getAttribute("foto");%>
<%String amigos = (String)request.getAttribute("amigos");%>

<img id="background" src="images/home_bg.png">

<div id=header>
	<p class="lobby_data"> Lobby <%=id_lobby%> </p><br>
	<p id="pass" class="lobby_data"> Pass: <%=pass%> </p>
</div>

<div id="friends" class="modal">

  <div class="modal-content">
    <span class="close" onclick="ocultar()">&times;</span>
   	<p style="font-family: 'Patrick Hand', cursive;font-size: 25px">Amigos:</p>
   	<div id="friend_list">
   	
   	</div>
  </div>

</div>

<div id="players">
	<div class="player" id="player_div_1" >
		<p class="player_name" id="p1_name"><%=user%></p>
		<div class="portrait" id="portrait_div_1" >
		</div>
	</div>
	<div class="player" id="player_div_2" >
		<p class="player_name" id="p2_name">Esperando...</p>
		<div class="portrait" id="portrait_div_2" >
		</div>
	</div>
	<div class="player" id="player_div_3">
		<p class="player_name" id="p3_name">Esperando...</p>
		<div class="portrait" id="portrait_div_3" >
		</div>
	</div>
	<div class="player" id="player_div_4" >
		<p class="player_name" id="p4_name">Esperando...</p>
		<div class="portrait" id="portrait_div_4" >
		</div>
	</div>
	<audio autoplay loop>
		<source src="music/home_music.mp3" type="audio/mpeg">
	</audio>
</div>

<div id=addPlayers>
	<button class="addPlayer" id="inviteFriend" onclick="showFriends()">Invitar amigos</button>
	<button class="addPlayer" id="addAI">Añadir Bot</button>
</div>

<button id="exitButton" onclick="backHome()">Salir</button>
<button id="startButton" onclick="startGame()">Empezar</button>

</body>
<script>

    const p2name = document.getElementById("p2_name");
    var p2img = "";
    const p3name = document.getElementById("p3_name");
    var p3img = "";
    const p4name = document.getElementById("p4_name");
    var p4img = "";
    
    const startButton = document.getElementById("startButton")

    var user = "<%=user%>";
    var am_i_host = "<%=am_i_host%>";
    var lobby = "<%=id_lobby%>";
    var pass = "<%=pass%>";

    const socket = new WebSocket("<%=Rck_conn.LOBBYWS%>");
    socket.binaryType = "arraybuffer";
    
	const friend_socket = new WebSocket("<%=Rck_conn.INVITARWS%>");
    friend_socket.binaryType = "arraybuffer";
    
    if (pass=="null"){
    	document.getElementById("pass").style.display='none';
    }
    
    const friendBox = document.getElementById("friends");
    const friendList = document.getElementById("friend_list");
    var friends = <%=amigos%>;
    for (var i=0;i<friends.length;i++){
		var entry = '<div id="_'+friends[i]+'" class="friend" onclick="inviteFriend(\''+friends[i]+'\')">'+friends[i]+'</div>';
		friendList.innerHTML+=entry;
    }
    
    var next_user = 2;
    
    var get_out = true;
    
    socket.onopen = function (event) {
    	var loginJSON = { msg_type: 'crear', id_user:user , id_lobby:lobby , image:'<%=foto%>'};
    	socket.send(JSON.stringify(loginJSON));
    };

    socket.onmessage = function (event) {
    	var msg = JSON.parse(event.data);
    	if(msg.msg_type=='connected'){
    		switch(next_user){
    			case 2:
    				p2name.innerHTML=msg.id_user;
    				p2img=msg.image;
    				break
    			case 3:
    				p3name.innerHTML=msg.id_user;
    				p3img=msg.image;
    				break
    			case 4:
    				p4name.innerHTML=msg.id_user;
    				p4img=msg.image;
    		}
    		next_user++
    		if (am_i_host=='true' && next_user>2){
    			startButton.style.display="block";
    		}
    	} else if (msg.msg_type=='disc') { 
    		next_user--;
    		switch(msg.id_user){
    			case p2name.innerHTML:
    				p2name.innerHTML=p3name.innerHTML;
    				p2img=p3img;
    				p3name.innerHTML=p4name.innerHTML;
    				p3img=p4img;
    				break
    			case p3name.innerHTML:
    				p3name.innerHTML=p4name.innerHTML;
    				p3img=p4img;
    				break
    		}
    		if (am_i_host=='true' && next_user<=2){
    			startButton.style.display="none";
    		}
    		if (msg.host) {am_i_host='true';}
    		if (am_i_host=='true' && next_user>2){
    			startButton.style.display="block";
    		}
			p4name.innerHTML="Empty";
    	} else if (msg.msg_type=="empezar"){
        	socket.close();
    		get_out=false;
        	window.location.replace("empezar_partida?id_user="+user+"&id_lobby="+lobby+"&host="+am_i_host);
    	} else { //kick
    		socket.close();
    		get_out=false;
    		window.location.replace("abandonar_lobby?id_user="+user+"&id_lobby="+lobby+"&expulsado=true");
    	}
    };
    
    function showFriends() {
    	friendBox.style.display="block";
    	window.onclick = function(event) {
   		  if (event.target == friendBox) {
   		    friendBox.style.display = "none";
   		  }
   		}
    }
    
    function ocultar()
    {
    	friendBox.style.display="none";
    }
    
    function inviteFriend(friend){
       	var inviteJSON = { msg_type: 'invitar', id_user:user , id_lobby:lobby , pass:pass , id_invitar:friend };
       	friend_socket.send(JSON.stringify(inviteJSON));
    }
    
    function backHome() {
    	get_out = false;
    	var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:lobby , portabilidad:false};
    	socket.send(JSON.stringify(exitJSON));
    	socket.close();
    	window.location.replace("abandonar_lobby?id_user="+user+"&id_lobby="+lobby+"&expulsado=false");
    }
    
    function fancyExit() {
    	if (get_out) {
    		var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:lobby , portabilidad:false};
    		sessionStorage.setItem("notsent_lobby",JSON.stringify(exitJSON));
        	sessionStorage.setItem("pending","abandonar_lobby?id_user="+user+"&id_lobby="+lobby+"&expulsado=false");
        	socket.close();
        	return true;
    	}
    }
	
    function startGame() {
    	get_out = false;
    	var startJSON = { msg_type: 'empezar', id_user:user , id_lobby:lobby };
    	socket.send(JSON.stringify(startJSON));
    	socket.close();
    	window.location.replace("empezar_partida?id_user="+user+"&id_lobby="+lobby+"&host="+am_i_host);
    }
    
    function miranda() {
    	if (get_out) {
    		sessionStorage.setItem("notsent_lobby",JSON.stringify(exitJSON));
        	sessionStorage.setItem("pending","abandonar_lobby?id_user="+user+"&id_lobby="+lobby+"&expulsado=false");
    		return "Volver atrás en el navegdor o recargar la página lo expulsará del lobby"
    	}
    }
    
    
</script>
</html>