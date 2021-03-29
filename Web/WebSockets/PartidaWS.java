package WS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.*;

import org.json.*;

import dao.ManosDAO;
import dao.ManosDAO.Mazo;

@ServerEndpoint("/PartidaWS")
public class PartidaWS {
	
	public class Partida {
		public ArrayList<String> mazo; //Cartas mazo de la partida
		public Integer numuser; //Num usuarios de la partida
		public Map<String,Session> usuarios; //Sesiones de los usuarios de la partida key: id_user
		public Map<String,Integer> numcartas; //Num cartas que deberá robar el usuario key:id_user
		public ArrayList<String> turno; //Orden turnos
		public Integer id_turno; //Componente del vector turno para conocer el turno actual
		public Map<String,ArrayList<String>> kicks; //Control kicks sala key:id_kick
		public Partida(Integer numuser) {
			this.numuser = numuser;
			mazo = new ArrayList<String>();
			usuarios = new HashMap<String,Session>();
			turno = new ArrayList<String>();
			numcartas = new HashMap<String,Integer>();
			id_turno = 0;
			kicks = new HashMap<String,ArrayList<String>>();
		}
		
	}
	//Map que contendrá las partidas activas junto a los datos de esta key: id_lobby
	static Map<Integer,Partida> connected = new HashMap<Integer,Partida>();	
	
	/**
	 * De forma general, envia mensajes de chat del sistema formato: msg_type:"chat" message:(string, mensaje a mostrar por pantalla)
	 * 
	 * 
	 * @param message (BASE) JSONObject con: id_user:(string,nombre usuario),id_lobby:(integer,id lobby),msg_type:(string,funcion a realizar)
	 * 		  Si msg_type = "empezar" añadimos como param numuser:(integer, num usuarios lobby) y tambien se añade host:(false,true) y si host es true mazo: (Json array strings)
	 * 		  
	 * 		  Si msg_type = "paso_turno" solo los parametros base
	 * 		  
	 * 		  Si msg_type = "jugar_carta" añadimos id_carta:(string,nombre carta) id_contrario:(string, nombre jugador al que robas solo en caso de que id_carta sea Robar o Marcianos)
	 *						 fin:(bool, solo si id_carta:"Salvacion" true si no quedan mas bombas a tratar, false en caso contrario)
	 *		  
	 *		  Si msg_type = "muerte" añadir numbombas:(integer, bombas que faltan de tratar)
	 *	
	 *		  Si msg_type = "salir" añadir numbombas:(integer, bombas que el usuario ha recibido)
	 *
	 *		  Si msg_type = "chat" añadir message:(string, mensaje a enviar)
	 *
	 *		  Si msg_type = "kick" añadir id_kick:(string, nombre de la persona a la que se expulsa) 
	 *
	 * @return Devuelve mensajes en formato JSON
	 *
	 * 		   Si msg_type = "empezar" devolverá mensajes msg_type:"nuevo_jugador" id_user:(string, nombre usuario sala)
	 * 						 Además, si han entrado todos los jugadores enviara msg_type:"turno" id_user:(string, nombre usuario que tiene el turno activo) a todos los jugadores
	 *		   Si msg_type = "paso_turno" devolverá msg_type:"roba_carta" JSONArray id_carta:(string, nombre carta) numbombas:(integer,numero de bombas en las cartas robadas)
	 *					     Además envia el nuevo turno: enviara msg_type:"turno" id_user:(string, nombre usuario que tiene el turno activo) a todos los jugadores
	 *		   Si msg_type = "jugar_carta"
	 *			- Si id_carta="Eureka", enviamos a id_user mensaje con msg_type:"eureka" y con JSONArray con las cartas que ve id_carta:["Eureka","Salvacion","Desastre"].	
	 *			- Si id_carta="Escape", enviamos a todos mensaje msg_type:"turno",id_user:(string,nombre de la persona que empieza)(pasar turno) y pasa sin robar.
	 *			- Si id_carta="Barajar", se baraja el mazo (no devuelve nada)
	 *			- Si id_carta="Sabotaje",enviamos a todos msg_type: turno,id_user:(string,nombre de la persona que empieza) sin robar
	 *			- Si id_carta="Robar" o "Marciano1/2/3/4", enviamos a id_user msg_type:"robar" y id_carta:(string,nombre carta robada), enviamos a id_contrario msg_type:"robado" y id_carta:(string, nombre carta que le roban)
	 *			- Si id_carta="Salvacion", si fin:true pasas turno msg_type:"turno",id_user:(string,nombre de la persona que empieza)y si no, no se envia nada. 
	 *		
	 *		   Si msg_type = "muerte", 
	 *			- Si el numero de jugadores que quedan es 1, envia msg_type:"ganar",id_user:(string, nombre del que gana) a todos
	 *		    - Si continua la partida, envia msg_type:"muerte",id_user:(string, nombre persona que pierde) a todos y
	 *			  envia turno msg_type:"turno",id_user:(string,nombre de la persona que empieza) a todos.
	 *		   
	 *		   Si msg_type = "salir",
	 *			- Si era el turno del usuario que ha salido, reenviamos turno msg_type:"turno",id_user:(string,nombre de la persona que empieza) a todos y msg_type:"muerte",id_user:(string, nombre persona que pierde) a todos
	 *			- Si no era el turno del usuario pero seguia jugando, enviamos msg_type:"muerte",id_user:(string, nombre persona que pierde) a todos
	 *		  
	 *		   Si msg_type = "chat", enviamos msg_type:"chat_u",message:(string, mensaje del usuario)	
	 *
	 *		   Si msg_type = "kick", enviamos lo mismo que al salir
	 */			
	 
	
	@OnMessage
	public void handleTextMessage(String message, Session session) {
		
		JSONObject obj = new JSONObject(message);		//Mensaje recibido
		String id_user = obj.getString("id_user");		//Id del usuario que ha enviado el mensaje
		Integer id_lobby = obj.getInt("id_lobby");	//Id del lobby el que se quiere unir o que se quiere crear
		String msg_type = obj.getString("msg_type");	//Parametro que indica si se quiere crear la sala, se quiere salir de esta o se quiere empezar la partida
		ManosDAO facade = new ManosDAO();
		JSONObject chat = new JSONObject();
		chat.put("msg_type", "chat");
		Partida partida;
		
		switch (msg_type) {
			case "empezar":
				
				//Si ya existe la partida-> lo uno
				if (connected.containsKey(id_lobby)) {
					partida = connected.get(id_lobby);	//Map que como key utiliza el id de los usuarios dentro de ese lobby y que contiene la sesiones de estos
					Iterator<String> it = partida.usuarios.keySet().iterator();
					
					while (it.hasNext()) {
						
						String k = it.next();					//Identificador del usuario que estaba dentro de la sala al que hay que informar
						Session s = partida.usuarios.get(k);	//Sesion del usuario que ya se encontraba dento de la sala
						
						try {
							
							//Resto de usuarios
							JSONObject aux = new JSONObject().put("id_user",id_user);
							aux.put("msg_type","nuevo_jugador");
							chat.put("message",id_user + " se ha unido a la partida " + partida.turno.size() + "/" + partida.numuser);
							s.getBasicRemote().sendText(chat.toString());
							
							String reply = aux.toString();
							s.getBasicRemote().sendText(reply);
							
							//Usuario actual
							aux = new JSONObject().put("id_user",k);
							aux.put("msg_type","nuevo_jugador");
							
							reply = aux.toString();
							session.getBasicRemote().sendText(reply);
							
						} catch (IOException e) {
							
							e.printStackTrace();
						
						}
					}
					
					partida.usuarios.put(id_user,session);
					partida.turno.add(id_user);
				}
				//Crear la partida
				else {
					partida = new Partida(obj.getInt("numuser"));
					partida.usuarios.put(id_user, session);
					partida.turno.add(id_user);
				}
				//Seleccionar el numero de cartas a robar al principio, 1
				partida.numcartas.put(id_user, 1);
				
				//Si es el host, añado el mazo a la partida
				if (!obj.isNull("host") && obj.getBoolean("host")) {
					JSONArray mazo = obj.getJSONArray("mazo");
					for (int i=0;i<mazo.length();i++) {
						partida.mazo.add(mazo.getString(i));
					}
				}
				
				//Comienza la partida envio el turno del comienzo
				if (partida.numuser == partida.usuarios.size()) {
					
					Collections.shuffle(partida.turno,new Random());
					chat.put("message", "¡Comienza la partida!");
					enviarMsj(partida,chat);
					enviarTurno(partida);
				}
				
				connected.put(id_lobby,partida);
				
				
				break;
			case "paso_turno":
				//Cogemos las cartas a robar 
				partida = connected.get(id_lobby);
				JSONObject carta = new JSONObject();
				carta.put("msg_type","roba_carta");
				//Robamos tantas cartas como indique el vector numcartas
				
				JSONArray crobadas = new JSONArray();
				boolean bomba=false; //Variable para colocar la segunda carta siempre una carta de desastre
				Integer numbombas=0;
				Integer numcartas = partida.mazo.size();
				for (int i=0;i<partida.numcartas.get(id_user) && i<numcartas;i++) {
					//Entras si solo robas 1 carta o si robas 2 entras si es la 2 carta o si no es una carta de desastre
					if (partida.numcartas.get(id_user)==1 || !partida.mazo.get(0).equals("Desastre") || (i==1) ){
						crobadas.put(partida.mazo.get(0)); //Anyado al json la carta robada
						if(partida.mazo.get(0).equals("Desastre")) {numbombas++;}
					}
					else { //Me sale una bomba en la primera carta robada cuando robo 2
						bomba=true;
						numbombas++;
					}
					
					facade.modificarMano(id_user,partida.mazo.get(0),"anyadir"); //anyado la carta a la mano del jugador
					partida.mazo.remove(0); //Elimino la carta del mazo
				}
				//Anyado carta bomba al final
				if (bomba) {
					crobadas.put("Desastre");
				}
				carta.put("numbombas", numbombas);
				carta.put("id_carta", crobadas);
				//Robara una carta en el siguiente turno
				partida.numcartas.put(id_user,1);
				
				//Actualizamos id_turno
				partida.id_turno= (partida.id_turno+1)%partida.numuser;

				//Enviar a id_user el mensaje con la nueva carta
				try {
					session.getBasicRemote().sendText(carta.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Enviar al resto el nuevo turno
				enviarTurno(partida);
				connected.put(id_lobby,partida);

				break;
			case "jugar_carta":
					partida = connected.get(id_lobby);
					//Jugar cartas
					cartas(partida,obj,session,facade);
					//Eliminar la/las cartas utilizadas de la mano del jugador
					String id_carta = obj.getString("id_carta");
					facade.modificarMano(id_user, id_carta, "eliminar");
					String aux = id_carta.substring(0,id_carta.length()-1);
					//Si era una carta Marciano1/2/3/4 elimino su pareja
					if (aux.equals("Marciano")) {
						facade.modificarMano(id_user, id_carta, "eliminar");
					}
					
				break;
			
			case "muerte":
					partida = connected.get(id_lobby);
					//Eliminar mano del jugador de la BD
					facade.eliminarMano(id_user);
					partida.numcartas.remove(id_user);//Eliminar al jugador del vector numcartas
					partida.numuser--; //Eliminamos el num usuarios
					partida.turno.remove(id_user); //Eliminamos al usuario de turnos
					partida.id_turno=partida.id_turno%partida.numuser; //Modificar turno
					partida.kicks = eliminarKicks(partida,id_user); //Eliminas los votos del kick del jugador
					
					chat.put("message",id_user + " ha explotado");
					enviarMsj(partida,chat);
					
					JSONObject msj = new JSONObject();
					//Paso de turno o enviar ganar
					if (partida.numuser==1) {
						//Sacar el que queda en turnos porque es el ganador y notificar
						msj.put("msg_type","ganar");
						msj.put("id_user", partida.turno.get(0));
						chat.put("message",partida.turno.get(0) + " ha ganado la partida");
						enviarMsj(partida,chat);
					}	
					else {
						msj.put("msg_type","muerte");
						msj.put("id_user",id_user);
						if(obj.getInt("numbombas")!=0) {
							//Anyadir carta bomba al mazo en una posicion aleatoria de este
							Integer random = (int) (Math.random()*(partida.mazo.size()+1));
							partida.mazo.add(random, "Desastre");
						}
						
					}
					//Notificar a todos los jugadores de la sala el ganador o el que ha perdido
					enviarMsj(partida,msj);
					
					
					if (partida.numuser!=1) { enviarTurno(partida); }

					//Guardo la nueva partida
					connected.put(obj.getInt("id_lobby"),partida);
				break;
				
			case "salir"://Jugador sale de la partida
				partida = connected.get(id_lobby);
				
				//Compruebas si solo queda un jugador 
				if (partida.usuarios.size()==1) {
					//Eliminar partida y eliminar al usuario la mano, el lobby y eliminar el mazo BD
					connected.remove(id_lobby);
					facade.eliminarMano(id_user);
					facade.eliminarMazo(id_lobby);
				}
				else { //Mas de un jugador
					partida = salirJugador(partida,id_user,facade,obj);
					connected.put(id_lobby, partida);
					chat.put("message",id_user + " ha abandonado la partida");
					enviarMsj(partida,chat);
					
				}
				facade.eliminarLobbyJugador(id_user);
				break;
			
			case "chat": //Envio de mensajes chat entre usuarios 
				partida = connected.get(id_lobby);
				chat.put("msg_type", "chat_u");
				chat.put("message", id_user + ": " + obj.getString("message"));
				enviarMsj(partida,chat);
				break;
				
			case "kick":
				partida = connected.get(id_lobby);
				//Buscar la lista de personas que han votado kick a id_kick y anyadir un voto de id_user
				String id_kick = obj.getString("id_kick");
				ArrayList<String> aux_kick;
				if (!partida.kicks.containsKey(id_kick)) {
					aux_kick = new ArrayList<String>();
				}
				else {
					aux_kick = partida.kicks.get(id_kick);
				}
				aux_kick.add(id_user);
				
				//Comprobar si tiene que ser expulsado
				if (aux_kick.size()>(partida.numuser/2)) {
					//Eliminamos al jugador partida
					obj.put("numbombas", facade.numBombas(id_kick));
					partida = salirJugador(partida,id_kick,facade,obj);
					//Eliminamos el lobby asociado
					facade.eliminarLobbyJugador(id_kick);
					//Notificamos su kick al chat
					chat.put("message",id_kick + " ha sido expulsado de la partida");
					enviarMsj(partida,chat);
				}
				else {
					//Anyadimos un kick a esa persona
					partida.kicks.put(id_kick,aux_kick);
				}
				connected.put(id_lobby, partida);
				break;
		}
				
	}
	public void cartas(Partida partida, JSONObject obj, Session session, ManosDAO facade) {
		JSONObject chat = new JSONObject();
		chat.put("msg_type","chat");
		
		switch (obj.getString("id_carta")) {
			
			case "Eureka": //Visualiza las 3 primeras cartas del mazo
				chat.put("message", obj.getString("id_user") + " ha visto el futuro");
				enviarMsj(partida,chat);
				//Genero JSONObject con las 3 primeras cartas
				JSONObject cartas = new JSONObject();
				cartas.put("msg_type","eureka");
				JSONArray eureka = new JSONArray();
				for (int i=0;i<3 && i<partida.mazo.size();i++) {
					eureka.put(partida.mazo.get(i));
				}
				cartas.put("id_carta", eureka);
				
				try {
					session.getBasicRemote().sendText(cartas.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			case "Escape": //Pasa turno sin robar
				chat.put("message", obj.getString("id_user") + " ha escapado");
				enviarMsj(partida,chat);
				
				partida.numcartas.put(obj.getString("id_user"),1); //Anula robar +2
				partida.id_turno= (partida.id_turno+1)%partida.numuser;
				enviarTurno(partida);
				connected.put(obj.getInt("id_lobby"),partida);
				break;
				
			case "Barajar": //Barajar mazo
				chat.put("message", obj.getString("id_user") + " ha barajado el mazo");
				enviarMsj(partida,chat);
				
				Collections.shuffle(partida.mazo,new Random());
				connected.put(obj.getInt("id_lobby"),partida);
				break;
				
			case "Sabotaje": //El prox jugador deberá robar 2 cartas y pasas turno sin robar
				chat.put("message", obj.getString("id_user") + " ha saboteado a " + partida.turno.get((partida.id_turno+1)%partida.numuser) + ", en el siguiente turno debera robar 2 cartas");
				enviarMsj(partida,chat);
				
				partida.id_turno= (partida.id_turno+1)%partida.numuser;//Pasas turno
				partida.numcartas.put(obj.getString("id_user"),1); //Anula robar +2
				partida.numcartas.put(partida.turno.get(partida.id_turno), 2); //A esa persona le añades 2 cartas a robar
				enviarTurno(partida); //Enviamos el turno
				connected.put(obj.getInt("id_lobby"),partida);
				break;
			
			case "Salvacion":
				chat.put("message", obj.getString("id_user") + " se ha salvado por los pelos!!");
				enviarMsj(partida,chat);
				
				//Compruebas si le quedan cartas por tratar (ej: 2 bombas)
				if(obj.getBoolean("fin")) {
					//Pasas turno
					partida.id_turno= (partida.id_turno+1)%partida.numuser;
					enviarTurno(partida);
				}
				//Borramos la carta bomba de la mano 
				facade.modificarMano(obj.getString("id_user"),"Desastre","eliminar");
				//Anyadir carta bomba al mazo en una posicion aleatoria de este
				Integer random = (int) (Math.random()*(partida.mazo.size()+1));
				partida.mazo.add(random, "Desastre");
				connected.put(obj.getInt("id_lobby"),partida);
				break;
			
			default: //Roba una carta de un jugador o Marcianos1/2/3/4
				chat.put("message", obj.getString("id_user") + " ha robado una carta a " + obj.getString("id_contrario") + " utilizando la carta " + obj.getString("id_carta"));
				enviarMsj(partida,chat);
				
				String id_carta = robarCarta(obj); //Robar carta y actualizar BD
				
				//Enviar mensajes JSON
				JSONObject robar = new JSONObject();
				JSONObject robado = new JSONObject();
				robar.put("msg_type", "robar");
				robar.put("id_carta", id_carta);
				robado.put("msg_type", "robado");
				robado.put("id_carta", id_carta);
				try {
					session.getBasicRemote().sendText(robar.toString());
					partida.usuarios.get(obj.getString("id_contrario")).getBasicRemote().sendText(robado.toString());
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
		}
		
	}
	public String robarCarta(JSONObject obj) {
		ManosDAO facade =  new ManosDAO();
		//Barajar la mano del contrario y seleccionar una carta
		ArrayList<Mazo> manocontraria = facade.devolverMano(obj.getString("id_contrario"));
		ArrayList<String> manocontraria_s = new ArrayList<String>();
		for (int i = 0; i < manocontraria.size(); i++) {
			for(int j=0;j<manocontraria.get(i).getNum();j++) {
				manocontraria_s.add(manocontraria.get(i).getId_carta());
			}
		}
		Collections.shuffle(manocontraria_s);
		//Introducir esa carta en la mano de id_user y quitarla de id_contrario
		facade.modificarMano(obj.getString("id_user"), manocontraria_s.get(0),"anyadir");
		facade.modificarMano(obj.getString("id_contrario"), manocontraria_s.get(0),"eliminar");
		return manocontraria_s.get(0);
	}
	
	//Realiza el proceso de enviar el nuevo turno a los jugadores de partida
	public void enviarTurno(Partida partida) {
		JSONObject chat = new JSONObject();
		chat.put("msg_type", "chat");
		chat.put("message", "Es el turno de: " + partida.turno.get(partida.id_turno));
		enviarMsj(partida,chat);
		JSONObject turno = new JSONObject().put("id_user",partida.turno.get(partida.id_turno));
		turno.put("msg_type","turno");
		enviarMsj(partida,turno);
	}
	//Envia un mensaje a todos los jugadores de partida
	public void enviarMsj(Partida partida, JSONObject msj) {
		String reply = msj.toString();
		Iterator<String> it = partida.usuarios.keySet().iterator();
		while (it.hasNext()) {
			String k = it.next();			
			Session s = partida.usuarios.get(k);	
			try {
				s.getBasicRemote().sendText(reply);
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	public Partida salirJugador(Partida partida, String id_user, ManosDAO facade, JSONObject obj) {
		
		//Comprobar si no habia perdido
		if (partida.turno.contains(id_user)) { //SI NO HA PERDIDO
			
			Integer numbombas_fin = obj.getInt("numbombas");
			//Gestion bombas
			if (numbombas_fin==2) {
				//Añadir una bomba a una posicion aleatoria
				Integer random = (int) (Math.random()*(partida.mazo.size()+1));
				partida.mazo.add(random, "Desastre");
			}
			else if (numbombas_fin==0) {
				//Quitar una bomba del mazo porque no ha explotado
				partida.mazo.remove("Desastre");
			}
			
			partida.kicks = eliminarKicks(partida,id_user);
			facade.eliminarMano(id_user);
			partida.numuser--;
			partida.numcartas.remove(id_user);//Eliminar al jugador del vector numcartas
			String usuario_turno = partida.turno.get(partida.id_turno);
			partida.turno.remove(id_user); //Eliminamos al usuario de turnos
			
			//Notificar al resto de usuarios de que se ha salido
			JSONObject msj_2 =  new JSONObject();
			msj_2.put("msg_type","muerte");
			msj_2.put("id_user",id_user);
			enviarMsj(partida,msj_2);
			
			//Comprobar turno
			if(usuario_turno.equals(id_user)) { //Si tienes el turno
				partida.id_turno=partida.id_turno%partida.numuser; //Modificar turno
				enviarTurno(partida);
			}else { 
				partida.id_turno=partida.turno.indexOf(usuario_turno);
			}
		}
		partida.usuarios.remove(id_user);
		return partida;
	}
	
	public Map<String,ArrayList<String>> eliminarKicks(Partida partida,String id_user) {
		
		//Eliminamos los kicks hacia el jugador
		partida.kicks.remove(id_user);
		Iterator<String> it = partida.kicks.keySet().iterator();
		while (it.hasNext()) {
			String k = it.next();			
			ArrayList<String> s = partida.kicks.get(k);
			s.remove(id_user);
			partida.kicks.put(k, s);
		}
		return partida.kicks;
		
	}

}

