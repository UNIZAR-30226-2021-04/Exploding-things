package WebSockets;
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
	
	static Map<String,Map <String,Session>> connected = new HashMap<String,Map <String,Session>>();

	@OnMessage
	public void handleTextMessage(String message, Session session) {
		JSONObject obj = new JSONObject(message);
		String id_user = obj.getString("id_user");
		String id_lobby = obj.getString("id_lobby");
		String msg_type = obj.getString("msg_type");
		
		switch (msg_type) {
			case "crear":
				//Introduccion del usuario
				if (connected.containsKey(id_lobby)) { //Ya existe la sala
					Map<String,Session> users_lobby = connected.get(id_lobby);
					
					Iterator<String> it = users_lobby.keySet().iterator();
					while (it.hasNext()) {
						String k = it.next();
						Session s = users_lobby.get(k);
						try {
							//Enviar al resto de usuarios el nuevo usuario
							JSONObject aux = new JSONObject().put("id_user",id_user);
							aux.put("msg_type","connected");
							String reply = aux.toString();
							s.getBasicRemote().sendText(reply);
							
							//Enviar al nuevo usuario el resto de usuarios
							aux = new JSONObject().put("id_user",k);
							aux.put("msg_type","connected");
							reply = aux.toString();
							session.getBasicRemote().sendText(reply);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//Anyado al nuevo usuario
					users_lobby.put(id_user,session);
					connected.put(id_lobby,users_lobby);
					
				}
				else { //No existe la sala
					Map<String,Session> users_lobby = new HashMap<String,Session>();
					users_lobby.put(id_user, session);
					connected.put(id_lobby,users_lobby);
				}
			break;
			case "salir":
				//Eliminar al usuario
				Map<String,Session> users_lobby = connected.get(id_lobby);
				if (users_lobby.size()==1) {//Elimina lobby si solo quedaba ese usuario
					connected.remove(id_lobby); 
				}
				else { //Elimina al usuario de la lobby
					users_lobby.remove(id_user);
					connected.put(id_lobby,users_lobby);
					
					Iterator<String> it = users_lobby.keySet().iterator();
					while (it.hasNext()) {
						String k = it.next();
						Session s = users_lobby.get(k);
						try {
							//Enviar al resto de usuarios el usuario que ha salido
							JSONObject aux = new JSONObject().put("id_user",id_user);
							aux.put("msg_type","disc");
							String reply=aux.toString();
							s.getBasicRemote().sendText(reply);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				break;
		}
				
		}
			
}

