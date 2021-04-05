package WS;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.*;

import org.json.*;

@ServerEndpoint("/LobbyWS")
public class LobbyWS {
	
	static Map<String,Map <String,Session>> connected = new HashMap<String,Map <String,Session>>();	//Map que contendrá los lobbys activos que a su vez contendran los usuarios que hay dentro de estos junto a la sesion de cada uno

	/**
	 * Metodo que cuando recibe un mensaje en formato realiza la accion especificada en el parametro msg_type:
	 * 	crear: Une al jugador id_user a la lobby id_lobby. Notifica a este los jugadores que estan en la sala 
	 * 	       y al resto de jugadores que estan en la misma id_lobby les notifica sobre la llegada de un nuevo jugador.
	 * 	       Devuelve JSON (msg_type:connected id_user:id_user(string, usuarios sala/nuevo jugador)).
	 *	salir: Elimina al jugador id_user de la lobby y notifica al resto de jugadores sobre su salida. Si en message host:true, el sig. usuario de la lobby
	 *             sera el nuevo host. 
	 *	       Devuelve JSON (msg_type:disc id_user:id_user(string, usuarios sala/nuevo jugador) host:true/false(bool, true si es nuevo host de la lobby false en caso contrario)).
	 *	empezar: Notifica a los jugadores del mismo id_lobby para empezar la partida. 
	 * 		 Devuelve JSON msg_type:"empezar"
	 * @param message Mensaje recibido formato JSON con parametros id_user,id_lobby,msg_type (strings) y host (bool)
	 * @param session Sesion del usuario que ha enviado el mensaje
	 * @return JSON 
	 */
	@OnMessage
	public void handleTextMessage(String message, Session session) {
		
		JSONObject obj = new JSONObject(message);		//Mensaje a enviar
		String id_user = obj.getString("id_user");		//Id del usuario que ha enviado el mensaje
		String id_lobby = obj.getString("id_lobby");	//Id del lobby el que se quiere unir o que se quiere crear
		String msg_type = obj.getString("msg_type");	//Parametro que indica si se quiere crear la sala, se quiere salir de esta o se quiere empezar la partida
		Boolean host = obj.getBoolean("host");			//Booleano que indica si el que ha enviado el mensaje es el host de la sala
		
		switch (msg_type) {
			case "crear":
				
				if (connected.containsKey(id_lobby)) {
					
					/*
					 * Si la sala ya existe entonces se agnadira al usuario dentro de esta
					 */
					Map<String,Session> users_lobby = connected.get(id_lobby);	//Map que como key utiliza el id de los usuarios dentro de ese lobby y que contiene la sesiones de estos
					
					Iterator<String> it = users_lobby.keySet().iterator();
					
					/*
					 * En esta bucle se recorren las diferentes componentes del map y se envian mensajes
					 * a los usuarios informandoles de que un usuario acaba de entrar (al usuario que ha entrado
					 * se le informa de sobre los usuarios que ya estan dentro)
					 */
					while (it.hasNext()) {
						
						String k = it.next();			//Identificador del usuario que estaba dentro de la sala al que hay que informar
						Session s = users_lobby.get(k);	//Sesion del usuario que ya se encontraba dento de la sala
						
						try {
							
							/*
							 * Enviar a los usuarios que ya se encontraban dentro del lobby un mensaje
							 * que les indica que el nuevo usuario acaba de entrar
							 */
							JSONObject aux = new JSONObject().put("id_user",id_user);
							aux.put("msg_type","connected");
							
							/*
							 * Creacion y envio del mensaje a enviar
							 */
							String reply = aux.toString();
							s.getBasicRemote().sendText(reply);
							
						    /*
						     * Envio al nuevo usuario que el usuario al que se le acaba de informar
						     * se encuentra en la partida
						     */
							aux = new JSONObject().put("id_user",k);
							aux.put("msg_type","connected");
							
							reply = aux.toString();
							session.getBasicRemote().sendText(reply);
							
						} catch (IOException e) {
							
							e.printStackTrace();
						
						}
					}
					
					/*
					 * Se agnade al nuevo usuario en la lista de usuarios de lobby
					 */
					users_lobby.put(id_user,session);
					connected.put(id_lobby,users_lobby);
					
				}
				else {
					
					/*
					 * En caso de que no exista la sala de be agnadir al listado de estas
					 */
					Map<String,Session> users_lobby = new HashMap<String,Session>();
					users_lobby.put(id_user, session);
					connected.put(id_lobby,users_lobby);
					
				}
				
				break;
			
			case "salir":
				
				Map<String,Session> users_lobby = connected.get(id_lobby);	//Lista de usuarios que hay dentro del lobby del que se quiere salir
				
				if (users_lobby.size()==1) {
					/*
					 * En caso de que en el lobby solo quedase ese usuario se elimina el lobby de la lista
					 */
					connected.remove(id_lobby);
					
				}
				else {
					
					/*
					 * Si aparte del usuario que se quiere salir quedan otros usuarios se elimina de las lista
					 * de usuarios del lobby se elimina a este
					 */
					users_lobby.remove(id_user);
					connected.put(id_lobby,users_lobby);
					
					boolean new_host = false; //Booleano que indica si se ha asignado un nuevo host
					
					/*
					 * Se recorre la lista de usuarios dentro del lobby para informarles de que el usuario
					 * se ha ido de este
					 */
					Iterator<String> it = users_lobby.keySet().iterator();
					while (it.hasNext()) {
						
						String k = it.next();			//Nombre del usuario al que se va a notificar
						Session s = users_lobby.get(k);	//Sesion del usuario a notificar
						
						try {
							
							/*
							 * Notificar al usuario de que el usuario que ha enviado el mensaje ha salido del lobby
							 */
							JSONObject aux = new JSONObject().put("id_user",id_user);
							aux.put("msg_type","disc");			
							
							if (!new_host && host){
								
								/*
								 * En caso de que el usuario que se haya salido fuese el host del lobby se asigna
								 * a este usuario como nuevo host y se le notifica de ello
								 */
								new_host=true;
								aux.put("host", true);
								
							}
							else {
								
								/*
								 * Se le notifica que el no va ha ser el host del lobby
								 */
								aux.put("host", false);
								
							}
							
							/*
							 * Creacion y envio del mensaje de notificacion
							 */
							String reply=aux.toString();
							s.getBasicRemote().sendText(reply);
							
						} catch (IOException e) {
							
							e.printStackTrace();
							
						}
						
					}
					
				}
				
				break;
				
			case "empezar":

				Map<String,Session> users_p = connected.get(id_lobby);	//Map con todos los usuarios dentro del lobby
				
				/*
				 * Bucle que notifica a todos los usuarios de que la partida va a empezar
				 */
				Iterator<String> it = users_p.keySet().iterator();
				while (it.hasNext()) {
					
					String k = it.next();		//Id del usuario al que se le va a enviar el mensaje
					Session s = users_p.get(k);	//Sesion del usuario con esa id
					
					try {
					
						if (k != id_user) {
							
							/*
							 * Solo en caso de que el usuario al que se le va a enviar el mensaje sea distinto del
							 * que nos ha notificado la accion de empezar se le notificará del comienzo de la partida
							 */
							JSONObject aux = new JSONObject().put("msg_type","empezar");
							
							/*
							 * Creadion y envio del mensaje
							 */
							String reply=aux.toString();
							s.getBasicRemote().sendText(reply);
							
						}
						
					} catch (IOException e) {
						
						e.printStackTrace();
						
					}
					
				}
				
				//Borrar la lobby
				connected.remove(id_lobby);
				
				break;
				
		}
				
	}
			
}

