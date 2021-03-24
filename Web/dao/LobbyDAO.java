package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.ConnectionManager;
import database.PoolConnectionManager;

public class LobbyDAO {
	
	//Gestion usuarios lobby
	private static String createLobby = "INSERT INTO Lobby DEFAULT VALUES";
	private static String selectLobby = "SELECT TOP 1 id_lobby FROM Lobby ORDER BY id_lobby DESC";
	private static String deleteLobby = "DELETE FROM Lobby WHERE id_lobby=?";
	private static String adduserLobby = "UPDATE Users SET id_lobby=? WHERE Username=?";
	private static String selectUsersLobby = "SELECT nusers FROM Lobby WHERE id_lobby=?";
	private static String updateUserLobby = "UPDATE Lobby SET nusers=? WHERE id_lobby=?";
	private static String deleteUserLobby = "UPDATE Users SET id_lobby=NULL WHERE Username=?";
	private static String selectLobbys = "SELECT * FROM Lobby WHERE nusers<4 AND pstart=0";
	
	public Integer CrearLobby(String id_user) { 
		Integer id_lobby = -1;
		Connection conn = null;
		
		
		try {
			// Abrimos la conexión e inicializamos los parámetros 
			conn = ConnectionManager.getConnection(); 
			PreparedStatement crear = conn.prepareStatement(createLobby);
			PreparedStatement Sid = conn.prepareStatement(selectLobby);
			PreparedStatement uniraLobby = conn.prepareStatement(adduserLobby);
			
			// Ejecutamos la consulta 
			crear.execute();
			ResultSet SidR = Sid.executeQuery();
			
			SidR.next();
			id_lobby = SidR.getInt(1);
			
			uniraLobby.setInt(1, id_lobby);
			uniraLobby.setString(2,id_user);
			uniraLobby.execute();
			
			//Liberamos los recursos utilizados
			crear.close();
			Sid.close();
			SidR.close();
			uniraLobby.close();
			
		} catch(SQLException se) {
			se.printStackTrace();  
		
		} catch(Exception e) {
			e.printStackTrace(System.err); 
		} finally {
			PoolConnectionManager.releaseConnection(conn); 
		}
		
		return id_lobby;
	}
	public boolean UnirLobby(String id_user,Integer id_lobby) { 
		boolean result=false;
		Connection conn = null;
		
		
		try {
			// Abrimos la conexión e inicializamos los parámetros 
			conn = ConnectionManager.getConnection(); 
			PreparedStatement lobbyllena = conn.prepareStatement(selectUsersLobby);
			lobbyllena.setInt(1, id_lobby);
			ResultSet lobbyllenaR = lobbyllena.executeQuery();
			lobbyllenaR.next();
			int nusers = lobbyllenaR.getInt(1);
			
			if(nusers==4) {
				return result;
			}
			//Actualiza el numero de personas en la lobby
			PreparedStatement UsersLobby = conn.prepareStatement(updateUserLobby);
			UsersLobby.setInt(1, nusers+1);
			UsersLobby.setInt(2,id_lobby);
			
			PreparedStatement uniraLobby = conn.prepareStatement(adduserLobby);
			uniraLobby.setInt(1, id_lobby);
			uniraLobby.setString(2, id_user);

			// Ejecutamos la consulta 
			UsersLobby.execute();
			uniraLobby.execute();
			
			result=true;
			
			//Liberamos los recursos utilizados
			lobbyllena.close();
			lobbyllenaR.close();
			UsersLobby.close();
			uniraLobby.close();

		} catch(SQLException se) {
			se.printStackTrace();  
		
		} catch(Exception e) {
			e.printStackTrace(System.err); 
		} finally {
			PoolConnectionManager.releaseConnection(conn); 
		}
		
		return result;
	}
	public ArrayList<Integer[]> SeleccionarLobbys() { 
		ArrayList<Integer[]> lobbys = new ArrayList<Integer[]>();
		Connection conn = null;
		
		try {
			// Abrimos la conexión e inicializamos los parámetros 
			conn = ConnectionManager.getConnection(); 
			PreparedStatement Lobbys = conn.prepareStatement(selectLobbys);
			ResultSet LobbysR = Lobbys.executeQuery();
			while(LobbysR.next()) {
				lobbys.add(new Integer[] {LobbysR.getInt(1), LobbysR.getInt(2)});
			}

			
			//Liberamos los recursos utilizados
			Lobbys.close();
			LobbysR.close();

		} catch(SQLException se) {
			se.printStackTrace();  
		
		} catch(Exception e) {
			e.printStackTrace(System.err); 
		} finally {
			PoolConnectionManager.releaseConnection(conn); 
		}
		
		return lobbys;
	}
	public void DesconectarLobby(String id_user,Integer id_lobby) { 
		
		Connection conn = null;
		
		
		try {
			// Abrimos la conexión e inicializamos los parámetros 
			conn = ConnectionManager.getConnection(); 
			//Ponemos a null el lobby del usuario desconectado
			PreparedStatement deleteLobbyUser = conn.prepareStatement(deleteUserLobby);
			deleteLobbyUser.setString(1, id_user);
			deleteLobbyUser.execute();
			
			PreparedStatement lobbyvacia = conn.prepareStatement(selectUsersLobby);
			lobbyvacia.setInt(1,id_lobby);
			ResultSet lobbyvaciaR = lobbyvacia.executeQuery();
			lobbyvaciaR.next();
			int nusers = (lobbyvaciaR.getInt(1));
			
			if(nusers==1) {
				PreparedStatement delLobby = conn.prepareStatement(deleteLobby);
				delLobby.setInt(1, id_lobby);
				delLobby.execute();
				delLobby.close();
				
			}
			else {
				
				nusers--;
				PreparedStatement UsersLobby = conn.prepareStatement(updateUserLobby);
				UsersLobby.setInt(1, nusers);
				UsersLobby.setInt(2,id_lobby);
				UsersLobby.executeUpdate();
				UsersLobby.close();
				
			}
		
			deleteLobbyUser.close();
			lobbyvacia.close();
			lobbyvaciaR.close();
			

		} catch(SQLException se) {
			se.printStackTrace();  
		
		} catch(Exception e) {
			e.printStackTrace(System.err); 
		} finally {
			PoolConnectionManager.releaseConnection(conn); 
		}
	}
	
}