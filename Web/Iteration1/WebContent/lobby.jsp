<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Lobby</title>
<link rel=stylesheet href=css/lobbystyle.css type=text/css>
</head>
<body>

<header id="lobby_name">Sala</header>
<%=(String)request.getAttribute("id_lobby")%>
<%String user = (String)request.getAttribute("id_user");%>
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
</div>
<button onclick="backHome()">Salir</button>
</body>
<script>

    const p2name = document.getElementById("p2_name");
    const p3name = document.getElementById("p3_name");
    const p4name = document.getElementById("p4_name");

    const socket = new WebSocket("ws://rocketruckus.westeurope.azurecontainer.io:8080/LobbyWS");
    socket.binaryType = "arraybuffer";
    
    var user = "<%=user%>";
    var lobby = "<%=(String)request.getAttribute("id_lobby")%>";
    
    var next_user = 2;

    socket.onopen = function (event) {
    	var loginJSON = { msg_type: 'crear', id_user:user , id_lobby:lobby };
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
    		}
    	} else { //disc
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
			p4name.innerHTML="Empty";
    	}
    };
    
    function backHome() {
    	var loginJSON = { msg_type: 'salir', id_user:user , id_lobby:lobby };
    	socket.send(JSON.stringify(loginJSON));
    	socket.close();
    	window.location.replace("abandonar_lobby?id_user="+user+"&id_lobby="+lobby);
    }

    
    
</script>
</html>