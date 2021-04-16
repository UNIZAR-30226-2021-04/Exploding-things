<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Rocket Ruckus - Game</title>
<link rel=stylesheet href=css/gamestyle.css type=text/css>
</head>
<body onbeforeunload="return miranda()" onunload="return fancyExit()" onpageshow="return salisteMal()">
<div style="display:flex"> 
	<div id="chatbox" class="sidebar">
		<div id="chat_container">
			<div id="chat" >
			</div>
			<input onkeypress="sendMessage(event)" id="chat_input" placeholder="Escribe un mensaje para chatear" maxlength="50">
		</div>
	</div>
	<div id="game" class="mainscreen">
		<div id="portraits" class="innersidebar" >
			<div id="p1" class="rival" >
				<div class="portrait">1</div>
				<div>
					<p id="p1_name" class="player_name">Player1</p>
					<p id="p1_report" class="report">!</p>
				</div>
			</div>
			<div id="p2" class="rival" >
				<div class="portrait">2</div>
				<div>
					<p id="p2_name" class="player_name">Player2</p>
					<p id="p2_report" class="report">!</p>
				</div>
			</div>
			<div id="p3" class="rival" >
				<div class="portrait">3</div>
				<div>
					<p id="p3_name" class="player_name">Player3</p>
					<p id="p3_report" class="report">!</p>
				</div>
			</div>
			<div id="you" class="player" >
				<div class="playerportrait">4</div>
				<p id="name"><%=(String)request.getAttribute("id_user")%> (You)</p>
			</div>
		</div>
		<div id="board" class="innermainscreen">
			<div id="deck">
				<p id="decktext" class="cardname">Mazo</p>
			</div>
			<p id="cardcounter"></p>
			<div id="hand">
				<p> </p>
			</div>
		</div>
	</div>
</div>
	<button onclick="backHome()">Salir</button>
	<button onclick="cancelHeist()">Cancelar robo</button>
	<audio autoplay loop>
		<source src="music/ingame_music.mp3" type="audio/mpeg">
	</audio>
	<audio id="card_sfx">
		<source src="music/card_sfx.mp3" type="audio/mpeg">
	</audio>
</body>
<script>

	const p1 = document.getElementById("p1");
	const p1name = document.getElementById("p1_name");
	const p1report = document.getElementById("p1_report");
	const p2 = document.getElementById("p2");
    const p2name = document.getElementById("p2_name");
	const p2report = document.getElementById("p2_report");
	const p3 = document.getElementById("p3");
    const p3name = document.getElementById("p3_name");
	const p3report = document.getElementById("p3_report");
    const you = document.getElementById("you");
    
    //Variable que indexa el próximo hueco de jugador a rellenar al recibir una nueva conexión
    var next_user = 1;
    //Habilita el voto de expulsión. Un jugador expulsado no puede votar.
    var kicksEnabled = true;
    //Al recibir un turno, my_turn=true ; Al robar o jugar una carta que pase turno, my_turn=false;
    //Con my_turn=false, no se puede jugar una carta ni robar
    var my_turn = false;
    
    //Elemento HTML de la mano del jugador
    const player_hand = document.getElementById("hand");
    
    //Elementos HTML del mazo de cartas
    const deck = document.getElementById("deck");
	deck.addEventListener('click',drawCard);
    const decktext = document.getElementById("decktext");
    const cardcounter = document.getElementById("cardcounter");
    var card_sfx = document.getElementById("card_sfx");
    
    //Elementos HTML del chat
    const chatbox = document.getElementById("chat");
    const input = document.getElementById("chat_input");
    var chatEnabled = true;
    
    //id del usuario
    var user = "<%=(String)request.getAttribute("id_user")%>";
    //id del lobby
    var lobby = "<%=(String)request.getAttribute("id_lobby")%>";
    //mano inicial del usuario
    var hand = <%=(String)request.getAttribute("mano")%>;
    //'true' si el usuario era el host de la partida
    var am_i_host = "<%=(String)request.getAttribute("host")%>";
    //El host hace de tubería para almacenar el mazo en el nuevo endpoint de WS
    if(am_i_host=='true'){
    	var mazo = <%=(String)request.getAttribute("mazo")%>;
    }
    //Numero de usuarios que tenia la partida al empezar
    var numuser = "<%=(String)request.getAttribute("numuser")%>";
    
    //Variable para evitar cuadros adicionales en una salida limpia
    var cleanExit=false;
    //Variable para simbolizar el fin de partida
    var gameOver=false;

    //Websocket del usuario. Se usa para paso de mensajes con el servidor
    const socket = new WebSocket("ws://rocketruckus.westeurope.azurecontainer.io:8080/PartidaWS");
    //const socket = new WebSocket("ws://localhost:8080/RocketRuckus/PartidaWS");
    socket.binaryType = "arraybuffer";
    
    //Cartas de salvación en posesión
    var defuseCards = 0;
    //Cartas de marciano en posesión
    var aliens = [0,0,0,0];
    
    
    //Cartas de bomba robadas. Se actualiza al robar carta
    var numBombas = 0;
    //"Modo bomba". Define el comportamiento de las cartas al recibir una carta 'Desastre'
    var bombMode = false;
    
    //"Modo atraco". Define el comportamiento de las cartas mientras se roban cartas a otro
    //jugador y activa el onclick de los retratos, permitiendo robarles una carta.
    var heistMode = false;
    //Id HTML de la carta que ha triggereado el "Modo atraco". Si era un marciano, crimePartner es la pareja
    var heistCard = '';
    var crimePartner = '';
    
    //Se contabilizan las cartas iniciales
    for (var i=0 ; i<hand.length ; i++){
    	countCards(hand[i],1);
		viewCard(hand[i]);
    }

    socket.onopen = function (event) {
    	var loginJSON = { msg_type: 'empezar', id_user:user , id_lobby:lobby , host:am_i_host, mazo:mazo, numuser:numuser};
    	socket.send(JSON.stringify(loginJSON));
    };

    socket.onmessage = function (event) {
    	var msg = JSON.parse(event.data);
    	switch(msg.msg_type){
    		case 'nuevo_jugador':
    			addUser(msg.id_user);
    			break;
    		case 'turno':
	    		if(msg.id_user==user && numuser>1){
	    			my_turn=true;
	    		} else {
	    			my_turn=false;
	    		}
	    		cardcounter.innerHTML='Quedan '+msg.numcartas+' cartas';
	    		break;
    		case 'roba_carta':
	    		numBombas = msg.numbombas;
	    		var cartas = msg.id_carta;
	    		for(var i=0 ; i<cartas.length ; i++){
	    			if (cartas[i]!='Desastre'){
	    				addCard(cartas[i]);
	    			} else {
	    				//TODO - MOSTRAR BOMBA
		    	    	//TEMPORAL:
		    	    	decktext.innerHTML='BOMBA';
	    				bombMode=true;
	    				my_turn=true;
	    				if(defuseCards==0){
	    					dieHard();
	    					break;
	    				}
	    			}
	    		}
	    		my_turn=true;
	    		break;
    		case 'chat':
    			showMessage(msg.message,'sysmsg');
    			break;
    		case 'chat_u':
    			var msguser = msg.message.slice(0,msg.message.search(':'));
    			if(msguser==user){
    				showMessage(msg.message,'ownmsg');
    			}else{
    				showMessage(msg.message,'rivalmsg');
    			}
    			break;
    		case 'robar':
    			if(msg.id_carta!==undefined&&msg.id_carta!=''){
					addCard(msg.id_carta);
					var thief = document.getElementById(heistCard);
		    		if(thief.innerHTML.slice(0,-1)=='Marciano'){
		    			aliens[Number(thief.innerHTML.slice(-1))-1]-=2;
		    			removeCard(crimePartner);
		    		}
			    	removeCard(heistCard);
		    	}
		    	else {
		    		showMessage('Ese jugador no tiene cartas...','sysmsg');
		    	}
    			my_turn=true;
				break;
    		case 'eureka':
    			//TODO - MOSTRAR CARTAS
    			//TEMPORAL:
				showMessage('EUREKA: '+msg.id_carta,'sysmsg');
				break;
    		case 'robado':
    			missCard(msg.id_carta);
    			break;
    		case 'ganar':
    			//TODO - MENSAJE DE VICTORIA
    			//TEMPORAL:
    			if(msg.id_user==user){
					showMessage('HAS GANADO!','sysmsg');
					killUser(p1name.innerHTML);
					killUser(p2name.innerHTML);
					killUser(p3name.innerHTML);
    			}
    			gameOver=true;
    			myTurn=false;
    			break;
    		case 'muerte':
    			if(msg.id_user!=user){
	    			killUser(msg.id_user);
	    			numuser--;
	    			if(numuser==1&&!gameOver){
	    				showMessage('HAS GANADO!','sysmsg');
	    				myTurn=false;
	    			}
    			} else {
    				gameOver=true;
    			}
    			break;
    		case 'kick':
    			cleanExit=true;
    			window.location.replace("buscar_partidas?error=kick");
				break;
    	}
    };
    
    //Añade un nuevo usuario a la partida.
    //Se utiliza cuando se recibe una nueva entrada a la sala
    function addUser(newuser){
    	switch(next_user){
		case 1:
			p1name.innerHTML=newuser;
			p1.addEventListener('click',function(){stealCard(newuser);});
			p1.style.visibility='visible';
			p1report.addEventListener('click',function(){kickPlayer(newuser);p1report.style.visibility='hidden'});
			next_user++;
			break;
		case 2:
			p2name.innerHTML=newuser;
			p2.addEventListener('click',function(){stealCard(newuser);});
			p2.style.visibility='visible'
			p2report.addEventListener('click',function(){kickPlayer(newuser);p2report.style.visibility='hidden'});
			next_user++;
			break;
		case 3:
			p3name.innerHTML=newuser;
			p3.addEventListener('click',function(){stealCard(newuser);});
			p3report.addEventListener('click',function(){kickPlayer(newuser);p3report.style.visibility='hidden'});
			p3.style.visibility='visible';
			break;
		}
    }
    
    //Elimina el retrato de un usuario
    function killUser(deaduser){
    	switch(deaduser){
		case p1name.innerHTML:
			p1.style.visibility='hidden';
			break;
		case p2name.innerHTML:
			p2.style.visibility='hidden';
			break;
		case p3name.innerHTML:
			p3.style.visibility='hidden';
			break;
    	}
    }
    
    //Envía un mensaje para robar carta y desactiva el mazo
    function drawCard() {
	    if(my_turn&&!bombMode){
	    	card_sfx.play();
	    	var passJSON = { msg_type: 'paso_turno', id_user:user , id_lobby:lobby };
	    	socket.send(JSON.stringify(passJSON));
	    	my_turn=false;
	    }
    }
    
    //Añade una carta a la mano
    function addCard(card){
		countCards(card,1);
		viewCard(card);
    }
    
    //Añade visualmente una carta a la mano y le asigna su funcionalidad
    function viewCard(card) {
    	var new_card = document.createElement( 'div' );
    	var cardHash = Math.random().toString().substr(2,8); //Extrae 8 decimales de un numero random entre 0 y 1
    	new_card.id='card'+cardHash;
    	new_card.innerHTML=card;
    	new_card.className='card';
    	new_card.onclick = function(){playCard(card,new_card.id);};
    	player_hand.appendChild(new_card);
    }
    
    //Lleva la cuenta de las cartas de salvacion y las de marcianos
    function countCards(card,count){
    	switch(card){
		case 'Salvacion':
			defuseCards+=count;
			break;
		case 'Marciano1':
			aliens[0]+=count;
			break;
		case 'Marciano2':
			aliens[1]+=count;
			break;
		case 'Marciano3':
			aliens[2]+=count;
			break;
		case 'Marciano4':
			aliens[3]+=count;
			break;
		}
    }

    //Implementa la función de cada carta, en función de
    //si se está en modo bomba o no y de condiciones específicas
    function playCard(card,card_id){
    	//Las cartas solo funcionan en mi turno y si no estoy en modo atraco
    	if(my_turn && !heistMode){
	    	//En modo bomba, las únicas cartas activas son las de salvación
	    	if(bombMode){
	    		if(card=='Salvacion'){
	    			numBombas--; defuseCards--;
	    			removeCard(card_id);
	    			//TODO - RETIRAR CARTA DE BOMBA
	    			//TEMPORAL:
		    	    decktext.innerHTML='Mazo';
	    			var safeJSON = { msg_type: 'jugar_carta', id_user:user , id_lobby:lobby, id_carta:'Salvacion' };
	    			safeJSON.fin=numBombas==0;
	    	    	socket.send(JSON.stringify(safeJSON));
	    	    	if(numBombas>0){
		    	    	//TODO - MOSTRAR BOMBA
		    	    	//TEMPORAL:
		    	    	decktext.innerHTML='BOMBA';
	    				my_turn=true;
						if(defuseCards==0){
							dieHard();
						}
	    	    	} else {
	    	    		bombMode=false;
	    	    	}
	    	    	card_sfx.play();
	    		}
	    	}
	    	//Si no estamos en modo bomba, se juega el resto de cartas
	    	else {
	    		//Las cartas que roban a otros jugadores se tratan de forma especial
	    		//Si la carta empieza por "Marciano" (más cualquier número)
	    		if(card.slice(0,-1)=='Marciano'){
	    			var numAlien=Number(card.slice(-1));
	    			if(aliens[numAlien-1]>1){
	    				heistMode=true;
	    				heistCard=card_id;
	    				crimePartner=findPartner(card_id);
	    				//TODO - Incluir botón de cancelar, solo visible en heist mode
	    			}
	    		} else if (card=='Robar'){
					heistMode=true;
					heistCard=card_id;
					crimePartner='';
					//TODO - Incluir botón de cancelar, solo visible en heist mode
				//Si no es una carta que requiera más interacción, la jugamos
	    		} else if (card!='Salvacion'){
	    			var cardJSON = { msg_type: 'jugar_carta', id_user:user , id_lobby:lobby, id_carta:card };
	    	    	socket.send(JSON.stringify(cardJSON));
	    			removeCard(card_id);
	    		}
		    	card_sfx.play();
	    	}
    	}
    }
    
    //Dado un nombre de carta, quita de la mano una carta con el mismo nombre
    function missCard(card){
    	var cards=player_hand.children;
    	for (var i=0;i<cards.length;i++){
    		if(cards[i].innerHTML==card){
    			player_hand.removeChild(cards[i]);
    			countCards(card,-1);
    			break;
    		}
    	}
    }
    
    //Dado el id de una carta, la elimina de la mano
    function removeCard(card_id){
    	var card=document.getElementById(card_id);
    	card.parentNode.removeChild(card);
    }
    
    //Dado el id de un jugador, envía un robo de carta
    function stealCard(victim_id){
    	if(heistMode){
	    	my_turn=false;
    		heistMode=false;
    		var thief = document.getElementById(heistCard);
    		var cardJSON = { msg_type: 'jugar_carta', id_user:user , id_lobby:lobby, id_contrario:victim_id, id_carta:thief.innerHTML};
	    	socket.send(JSON.stringify(cardJSON));
    	}
    }
    
    //Encuentra un pareja para una carta
    function findPartner(card_id){
    	var Bonnie = document.getElementById(card_id);
    	var Clydes = Bonnie.parentElement.children;
    	for (var i=0;i<Clydes.length;i++){
    		if(Clydes[i].id!=card_id && Bonnie.innerHTML==Clydes[i].innerHTML){
    			return Clydes[i].id;
    		}
    	}
    }
    
    //Cancela el robo
    function cancelHeist(){
    	heistMode=false;
    	//TODO - Ocultar el botón de cancelar el modo atraco
    }
    
    //Envía el mensaje de muerte.
    //El usuario se encuentra en modo bomba y no tiene cartas de salvacion,
    //por lo que ningun boton dentro del juego esta operativo.
    function dieHard(){
    	var deathJSON = { msg_type: 'muerte', id_user:user , id_lobby:lobby , numbombas:numBombas-1};
		socket.send(JSON.stringify(deathJSON));
		//TODO - MENSAJE DE DERROTA
		//TEMPORAL:
		showMessage('HAS PERDIDO!','sysmsg');
		p1report.style.display='none';
		p2report.style.display='none';
		p3report.style.display='none';
		
    }
    
    //Envía un mensaje con el contenido del formulario
    //y vacía la entrada
    function sendMessage(event){
    	if (event.key=='Enter' && input.value!=''){
			var msgJSON = { msg_type: 'chat', id_user:user , id_lobby:lobby, message:input.value };
			socket.send(JSON.stringify(msgJSON));
			input.value='';
    	}
    }
    
    //Muestra un mensaje por la pantalla de chat
    function showMessage(msg,msgclass){
    	var mensaje = '<p class="'+msgclass+'">'+msg+'</p>';
		chatbox.innerHTML+=mensaje;
		chatbox.scrollTop=chatbox.scrollHeight;
    }
    
    //Dado un id de usuario, manda un voto para expulsar a 
    //ese usuario de la partida.
    function kickPlayer(user_id){
	    if(kicksEnabled){
	    	var kickJSON = { msg_type: 'kick', id_user:user , id_lobby:lobby, id_kick:user_id};
	    	socket.send(JSON.stringify(kickJSON));
	    }
    }
    
    function backHome() {
    	cleanExit=true;
    	var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:lobby, numbombas:numBombas};
    	socket.send(JSON.stringify(exitJSON));
    	console.log(JSON.stringify(exitJSON));
    	socket.close();
    	sessionStorage.removeItem("pending");
    	window.location.replace("buscar_partidas");
    }
    
    function fancyExit() {
    	if(!cleanExit){
    		var exitJSON = { msg_type: 'salir', id_user:user , id_lobby:lobby, numbombas:numBombas};
    		sessionStorage.setItem("notsent",JSON.stringify(exitJSON));
    		sessionStorage.setItem("salimal","sisoy");
    		socket.close();
    	}
    	return true;
    }
    
    function salisteMal() {
    	if(sessionStorage.getItem("salimal")){
        	sessionStorage.removeItem("salimal");
        	window.location.replace("buscar_partidas");
    	}
    }
    
    function miranda() {
    	if(!cleanExit){
    		return 'Salir de la página contará como derrota y te expulsará de la partida.'
    	}
    }
    
</script>
</html>