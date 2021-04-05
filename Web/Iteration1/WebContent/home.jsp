<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Home</title>
<link rel=stylesheet href=css/homestyle.css type=text/css>
</head>

<body>
<% String id_user = (String)request.getSession().getAttribute("id_user"); %>

<div class="game_div" >
	<p id="lobby_placeholder">No se han encontrado partidas públicas</p>
	<div id="lobby_list">
	</div>
	<form action="buscar_partidas">
	<button id="search" type="submit">Buscar partidas</button>
	</form>
	<form action="crear_partida">
	<input type=hidden name="id_user" value="<%=id_user%>"> 
	<!-- RETURN BACK TO MONKE -->
	<button id="search" type="submit">Crear partida</button>
	</form>
</div>
<div class="game_div" >
	<p>Aquí irán las partidas privadas</p>
</div>

</body>

<script>

    const lobbyList = document.getElementById("lobby_list");
    const placeholder = document.getElementById("lobby_placeholder");
    
    const prefix_lobby = '<a href="unirse_lobby?id_user=<%=id_user%>&id_lobby='
    const interfix_lobby = '"><div class="lobby">'
    const sufix_lobby = '</div></a><br>'
    
    var lobbys = <%=(String)request.getAttribute("lobby_list")%>;
    
    if(lobbys.length>0){
    	lobbyList.innerHTML="";
    	placeholder.innerHTML="";
        for (var i=0;i<lobbys.length;i++){
        	var lobby_i = lobbys[i];
    		var entry = prefix_lobby + lobby_i.id_lobby + interfix_lobby + lobby_i.id_lobby + ':' + lobby_i.nuser + "users" + sufix_lobby;
    		lobbyList.innerHTML+=entry;
        }
    }
    
</script>

</html>