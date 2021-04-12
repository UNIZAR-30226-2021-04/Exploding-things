package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import database.ConnectionManager;
import database.PoolConnectionManager;


/**
 * Clase encargada del sistema de amigos
 */
public class AmigosDAO {
	private String sendSolicitud = "INSERT INTO Amigos VALUES (?,?,1)";
	private String nAmigos = "SELECT count(*) FROM Amigos WHERE (user1=? OR user2=?) AND solicitud=0";
	private String aceptarSolicitud = "UPDATE Amigos SET solicitud=0 WHERE user1=? AND user2=?";
	private String deleteAmigo ="DELETE FROM Amigos WHERE user1=? AND user2=?";
	private String showAmigos = "SELECT user1,user2 FROM Amigos WHERE (user1=? OR user2=?) AND solicitud=0";
	private String showSolicitudes = "SELECT user1,user2 FROM Amigos WHERE user2=? AND solicitud=1";
	
	/**
	 * User envia una solicitud al usuario user_d 
	 * @param user Usuario que envia la solicitud
	 * @param user_d Usuario que recibe la solicitud
	 * @return bool, true si ha podido enviarla
	 */
	public boolean enviarSolicitud(String user,String user_d) { 
		
		boolean result = false;	//Resultado a devolver
		Connection conn = null;
		
		try {
			
			conn = ConnectionManager.getConnection(); 
			PreparedStatement amigos = conn.prepareStatement(nAmigos);
			amigos.setString(1, user);
			amigos.setString(2, user);
			ResultSet amigosRs = amigos.executeQuery();
			amigosRs.next();
			if (amigosRs.getInt(1)<30) {
				PreparedStatement solicitud = conn.prepareStatement(sendSolicitud);
				solicitud.setString(1,user);
				solicitud.setString(2,user_d);
				solicitud.execute();
				result=true;
				solicitud.close();
			}
			
			amigos.close();
			amigosRs.close();
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
		return result;
	}
	/**
	 * User acepta la solicitud de user_d
	 * @param user Usuario que acepta la solicitud
	 * @param user_d Usuario del que se acepta la solicitud
	 * @return bool, true si ha podido aceptarlo
	 */
	public boolean aceptarSolicitud(String user,String user_d) { 
		Connection conn = null;
		boolean result = false;
		try {
			
			conn = ConnectionManager.getConnection(); 
			PreparedStatement amigos = conn.prepareStatement(nAmigos);
			amigos.setString(1, user);
			amigos.setString(2, user);
			ResultSet amigosRs = amigos.executeQuery();
			amigosRs.next();
			PreparedStatement amigos_d = conn.prepareStatement(nAmigos);
			amigos_d.setString(1, user_d);
			amigos_d.setString(2, user_d);
			ResultSet amigosRs_d = amigos_d.executeQuery();
			amigosRs_d.next();
			if (amigosRs.getInt(1)<30 && amigosRs_d.getInt(1)<30) {
				PreparedStatement accept= conn.prepareStatement(aceptarSolicitud);
				accept.setString(1, user_d);
				accept.setString(2, user);
				result = accept.executeUpdate() > 0;
				accept.close();
			}
			amigos.close();
			amigosRs.close();
			amigos_d.close();
			amigosRs_d.close();
			

		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		return result;
	}
	/**
	 * User elimina a user_d de amigos
	 * @param user Usuario que elimina al amigo
	 * @param user_d Usuario al que se elimina de amigos
	 */
	public void eliminarAmigo(String user,String user_d) { 
		Connection conn = null;
		try {
			
			conn = ConnectionManager.getConnection(); 
			PreparedStatement rechazar = conn.prepareStatement(deleteAmigo);
			rechazar.setString(1, user_d);
			rechazar.setString(2, user);
			rechazar.execute();
			rechazar.close();
			

		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
	}
	
	/**
	 * Devuelve JSONArray de los usuarios que son amigos de user
	 * @param user Usuario del que se devuelve la lista de amigos
	 * @return JSONArray
	 */
	public JSONArray verAmigos(String user) { 
		Connection conn = null;
		JSONArray amigo = new JSONArray();
		try {
			
			conn = ConnectionManager.getConnection(); 
			PreparedStatement amigos = conn.prepareStatement(showAmigos);
			amigos.setString(1, user);
			amigos.setString(2, user);
			ResultSet amigosRs = amigos.executeQuery();
			String user1,user2;
			while(amigosRs.next()) {
				user1 = amigosRs.getString(1);
				user2 = amigosRs.getString(2);
				if (user1.equals(user)) {
					amigo.put(user2);
				}
				else {
					amigo.put(user1);
				}
			}
			amigos.close();
			amigosRs.close();
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		return amigo;
	}
	/**
	 * Devuelve JSONArray de los usuarios que han enviando solicitud a user
	 * @param user Usuario del que se devuelve la lista de solicitudes
	 * @return JSONArray
	 */
	public JSONArray verSolicitud(String user) { 
		Connection conn = null;
		JSONArray solicitud = new JSONArray();
		try {
			
			conn = ConnectionManager.getConnection(); 
			PreparedStatement solicitudes = conn.prepareStatement(showSolicitudes);
			solicitudes.setString(1, user);
			ResultSet solicitudesRs = solicitudes.executeQuery();
			String user1,user2;
			while(solicitudesRs.next()) {
				user1 = solicitudesRs.getString(1);
				user2 = solicitudesRs.getString(2);
				if (user1.equals(user)) {
					solicitud.put(user2);
				}
				else {
					solicitud.put(user1);
				}
			}
			solicitudes.close();
			solicitudesRs.close();
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		return solicitud;
	}
	
}