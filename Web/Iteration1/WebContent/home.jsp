<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Home</title>
<link rel=stylesheet href=css/homestyle.css type=text/css>
</head>

<body onpageshow="pending()">
<% String id_user = (String)request.getSession().getAttribute("id_user"); %>

<% String error=(String)request.getAttribute("error"); %>
<div class="error"><% if (error!=null){out.print(error);} %></div>
<div class="game_div" >
	<p id="lobby_placeholder">No se han encontrado partidas públicas</p>
	<div id="lobby_list">
	</div>
	<div>
	<button id="searchButton" onclick="buscar()">Buscar partidas</button>
	</div>
	<div>
	<button id="createButton" onclick="creada()">Crear partida</button>
	</div>
	<form action="logout">
	<button type="submit">Cerrar sesión</button>
	</form>
	<audio autoplay loop>
		<source src="music/home_music.mp3" type="audio/mpeg">
	</audio>
</div>
<div class="game_div" >
	<p>Aquí irán las partidas privadas</p>
</div>


</body>

<script>

    const lobbyList = document.getElementById("lobby_list");
    const placeholder = document.getElementById("lobby_placeholder");
    
    var lobbys = <%=(String)request.getAttribute("lobby_list")%>;
    var user = '<%=(String)request.getSession().getAttribute("id_user")%>';
    
    var uniendose=false;
    
    if(lobbys.length>0 && !sessionStorage.getItem("pending")){
    	lobbyList.innerHTML="";
    	placeholder.innerHTML="";
        for (var i=0;i<lobbys.length;i++){
        	var lobby_i = lobbys[i];
    		var entry = '<button type="submit" class="lobbybutton" onclick="unirse('+lobbys[i].id_lobby+')">'+'Sala' + (i+1) + ':' + lobby_i.nuser + "users" + '</button>';
    		lobbyList.innerHTML+=entry;
        }
    }
    
    function creada()
    {
    	document.getElementById("createButton").disabled = true;
    	sessionStorage.removeItem("salimal");
    	location.assign("crear_partida?id_user="+"<%=id_user%>");
    }
    
    function unirse(idlobby)
    {
        if(!uniendose){
        	uniendose=true;
        	sessionStorage.removeItem("salimal");
        	location.assign("unirse_lobby?id_user="+user+"&id_lobby="+idlobby);
        }
    }
    
    function buscar()
    {
    	document.getElementById("searchButton").disabled = true;
    	sessionStorage.removeItem("salimal");
    	location.replace("buscar_partidas");
    }
    
    function pending()
    {
    	if(sessionStorage.getItem("pending")){
    		var newloc = sessionStorage.getItem("pending");
    		sessionStorage.removeItem("pending");
    		location.replace(newloc);
    	}
    	if(sessionStorage.getItem("notsent")){
    		var exitJSON=sessionStorage.getItem("notsent");
    		console.log(exitJSON);
    		const socket = new WebSocket("ws://rocketruckus.westeurope.azurecontainer.io:8080/PartidaWS");
    	    //const socket = new WebSocket("ws://localhost:8080/RocketRuckus/PartidaWS");
    	    socket.binaryType = "arraybuffer";
    	    socket.onopen = function (event) {
    	    	socket.send(exitJSON);
    	    	socket.close();
    	    };
    	    sessionStorage.removeItem("notsent");
    	}
    	
    	document.getElementById("searchButton").disabled = false;
    	document.getElementById("createButton").disabled = false;
    	
    }
    
</script>

</html>