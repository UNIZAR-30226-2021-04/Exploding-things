<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Lobby</title>
<link rel=stylesheet href=css/lobbystyle.css type=text/css>
</head>
<body onbeforeunload= "return miranda()" onunload="return fancyExit()">

<%String user = (String)request.getAttribute("id_user");%>
<%String am_i_host = (String)request.getAttribute("host");%>
<div class="players">
	<div class="player" id="player_div_1" >
		<p class="player_name" id="p1_name"><%=user%></p>
		<div class="portrait" id="portrait_div_1" >
		</div>
	</div>
	<div class="player" id="player_div_2" >
		<p class="player_name" id="p2_name">Empty</p>
		<div class="portrait" id="portrait_div_1" >
		</div>
	</div>
	<div class="player" id="player_div_3">
		<p class="player_name" id="p3_name">Empty</p>
		<div class="portrait" id="portrait_div_1" >
		</div>
	</div>
	<div class="player" id="player_div_4" >
		<p class="player_name" id="p4_name">Empty</p>
		<div class="portrait" id="portrait_div_1" >
		</div>
	</div>
	<audio autoplay loop>
		<source src="music/home_music.mp3" type="audio/mpeg">
	</audio>
</div>
<button onclick="backHome()">Salir</button>
<button id="startButton" onclick="startGame()">Empezar</button>
</body>
<script>

    const p2name = document.getElementById("p2_name");
    const p3name = document.getElementById("p3_name");
    const p4name = document.getElementById("p4_name");
    
    const startButton = document.getElementById("startButton")

    const socket = new WebSocket("ws://rocketruckus.westeurope.azurecontainer.io:8080/LobbyWS");
    //const socket = new WebSocket("ws://localhost:8080/RocketRuckus/LobbyWS");
    socket.binaryType = "arraybuffer";
    var user = "<%=user%>";
    var am_i_host = "<%=am_i_host%>";
    var lobby = "<%=(String)request.getAttribute("id_lobby")%>";
    
    var next_user = 2;
    
    var get_out = true;

    socket.onopen = function (event) {
    	var loginJSON = { msg_type: 'crear', id_user:user , id_lobby:lobby , host:am_i_host};
    	socket.send(JSON.stringify(loginJSON));
    };

    socket.onmessage = function (event) {
    	var msg = JSON.parse(event.data);
    	if(msg.msg_type=='connected'){
    		switch(next_user){
    			case 2:
    				p2name.innerHTML=msg.id_user;
    				next_user++;
    				break
    			case 3:
    				p3name.innerHTML=msg.id_user;
    				next_user++;
    				break
    			case 4:
    				p4name.innerHTML=msg.id_user;
    				next_user++;
    		}
    		if (am_i_host=='true' && next_user>2){
    			startButton.style.display="block";
    		}
    	} else if (msg.msg_type=='disc') { 
    		next_user--;
    		switch(msg.id_user){
    			case p2name.innerHTML:
    				p2name.innerHTML=p3name.innerHTML;
    				p3name.innerHTML=p4name.innerHTML;
    				break
    			case p3name.innerHTML:
    				p3name.innerHTML=p4name.innerHTML;
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
    	} else { //empezar
        	socket.close();
    		get_out=false;
        	window.location.replace("empezar_partida?id_user="+user+"&id_lobby="+lobby+"&host="+am_i_host);
    	}
    };
    
    function backHome() {
    	get_out = false;
    	var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:lobby, host:am_i_host };
    	socket.send(JSON.stringify(exitJSON));
    	socket.close();
    	window.location.replace("abandonar_lobby?id_user="+user+"&id_lobby="+lobby);
    }
    
    function fancyExit() {
    	if (get_out) {
    		var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:lobby, host:am_i_host };
    		sessionStorage.setItem("notsent_lobby",JSON.stringify(exitJSON));
        	sessionStorage.setItem("pending","abandonar_lobby?id_user="+user+"&id_lobby="+lobby)
        	return true;
    	}
    }
	
    function startGame() {
    	get_out = false;
    	var startJSON = { msg_type: 'empezar', id_user:user , id_lobby:lobby, host:am_i_host };
    	socket.send(JSON.stringify(startJSON));
    	socket.close();
    	window.location.replace("empezar_partida?id_user="+user+"&id_lobby="+lobby+"&host="+am_i_host);
    }
    
    function miranda() {
    	if (get_out) {
    		return "Volver atrás en el navegdor o recargar la página lo expulsará del lobby"
    	}
    }
    
    
</script>
</html>