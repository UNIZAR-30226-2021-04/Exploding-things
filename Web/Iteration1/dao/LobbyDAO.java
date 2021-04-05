package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.ConnectionManager;
import database.PoolConnectionManager;

/**
 * Clase encargada de la gestión de los lobbys en la base de datos y de sacar la información relacionada
 * con estos.
 */

public class LobbyDAO {
	
	//Gestion usuarios lobby
	private static String createLobby = "INSERT INTO Lobby DEFAULT VALUES";							//Sentencia de creación de lobbys
	private static String selectLobby = "SELECT TOP 1 id_lobby FROM Lobby ORDER BY id_lobby DESC";	//Sentencia que selecciona el ultimo lobby creado
	private static String deleteLobby = "DELETE FROM Lobby WHERE id_lobby=?"; 						//Sentencia para la eliminacion de un lobby
	private static String adduserLobby = "UPDATE Users SET id_lobby=? WHERE Username=?"; 			//Sentencia para agnadir un usuario a un lobby
	private static String selectUsersLobby = "SELECT nusers FROM Lobby WHERE id_lobby=? AND pstart=0"; //Sentencia que selecciona el numero de usuarios dentro de un lobby
	private static String selectUsersLobby2 = "SELECT nusers FROM Lobby WHERE id_lobby=?";			//Sentencia que selecciona el numero de usuarios dentro de un lobby empezado
	private static String updateUserLobby = "UPDATE Lobby SET nusers=? WHERE id_lobby=?"; 			//Sentencia para modificar el numero de usuarios dentro de un lobby
	private static String deleteUserLobby = "UPDATE Users SET id_lobby=NULL WHERE Username=?"; 		//Sentencia para sacar a un usuario de una sala
	private static String selectLobbys = "SELECT * FROM Lobby WHERE nusers<4 AND pstart=0";			//Sentencia que devuelve los lobbys que aun no estan llenos y no han empezado
	
	/**
	 * Crea un nuevo lobby y mete al usuario dentro de el
	 * @param id_user Id del usuario que ha creado la sala
	 * @return Id del lobby que se acaba de crear o -1 en caso de que no se haya podido crear el lobby
	 */
	public Integer CrearLobby(String id_user) { 
		
		Integer id_lobby = -1;	//Id del lobby a devolver
		Connection conn = null;
		
		try {
			/*
			 * Abrimos la conexión y preparar las sentencias a ejecutar (sentencia de creacion de un lobby, 
			 * de selleccion del ultimo lobby creado y de agnadir un usuario a un lobby)
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement crear = conn.prepareStatement(createLobby);
			PreparedStatement Sid = conn.prepareStatement(selectLobby);
			PreparedStatement uniraLobby = conn.prepareStatement(adduserLobby);
			
			/*
			 * Ejecutamos la consulta  de creacion y de seleccion
			 */
			crear.execute();
			ResultSet SidR = Sid.executeQuery();
			
			/*
			 * Coje el id del lobby que se ha creado anteriormente
			 */
			SidR.next();
			id_lobby = SidR.getInt(1);
			
			/*
			 * Agnade los parametros a la sentencia para agnadir usuarios y la ejecuta
			 */
			uniraLobby.setInt(1, id_lobby);
			uniraLobby.setString(2,id_user);
			uniraLobby.execute();
			
			/*
			 * Liberamos los recursos utilizados
			 */
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
	
	/**
	 * Mete a un usuario dentro de un lobby ya existente
	 * @param id_user Id del usuario que ha solicitado unirse al lobby
	 * @param id_lobby Id del lobby al que se quiere unir el usuario
	 * @return True en caso de se haya podido agnadir al usuario dentro de la sala
	 */
	public boolean UnirLobby(String id_user,Integer id_lobby) { 
		
		boolean result=false;	//Parámetro a devolver
		Connection conn = null;
		
		try {
			/*
			 * Abrimos la conexión y preparamos e inicializamos los parámetros de la sentencia para
			 * seleccionar el numero de usuarios dentro de una sala
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement lobbyllena = conn.prepareStatement(selectUsersLobby);
			lobbyllena.setInt(1, id_lobby);
			
			/*
			 * Ejecutamos la sentencia preparada anteriormente
			 */
			ResultSet lobbyllenaR = lobbyllena.executeQuery();
			
			/*
			 * Cojemos el numero de usuarios que hay dentro de esa sala
			 */
			int nusers;
			if (lobbyllenaR.next()){
			    nusers = lobbyllenaR.getInt(1); //Numero de usuarios en la sala a la que se quiere unir
			}
			else{
				nusers=4;
			}
			/*
			 * Si el numero de usuarios es 4 quiere decir que otro u otros usuarios se nos han adelantado y
			 * el lobby esta lleno
			 */
			if(nusers==4) {
				return result;
			}
			
			/*
			 * Se prepara la sentencia para la actualizacion del numero de usuarios dentro de una sala
			 */
			PreparedStatement UsersLobby = conn.prepareStatement(updateUserLobby);
			UsersLobby.setInt(1, nusers+1);
			UsersLobby.setInt(2, id_lobby);
			
			/*
			 * Preparación de la sentencia para agnadir al usuario dentro de la sala
			 */
			PreparedStatement uniraLobby = conn.prepareStatement(adduserLobby);
			uniraLobby.setInt(1, id_lobby);
			uniraLobby.setString(2, id_user);

			/*
			 * Ejecutamos las consultas preparadas anteriormente
			 */
			UsersLobby.execute();
			uniraLobby.execute();
			
			result=true;
			
			/*
			 * Liberamos los recursos utilizados
			 */
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
	
	/**
	 * Metodo que devuelve los lobbys con huecos disponibles y que aun no han empezado a jugar
	 * @return ArrayList que contiene el id de los lobbys junto el numero de usuarios que tiene dentro (en caso de que no haya encontrado ninguno devuelve una lista vacia)
	 */
	public ArrayList<Integer[]> SeleccionarLobbys() { 
		ArrayList<Integer[]> lobbys = new ArrayList<Integer[]>();	//Lista que cotendra los lobbys a devolver
		Connection conn = null;
		
		try {
			
			/*
			 *  Abrimos la conexión, inicializamos los parámetros y ejecutamos la sentencia para sacar la 
			 *  informacion de los lobbys
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement Lobbys = conn.prepareStatement(selectLobbys);
			ResultSet LobbysR = Lobbys.executeQuery();
			
			/*
			 * Recorre el resultado de ejecutar la sentencia y agnade a la lista el id y el numero 
			 * de usuarios del lobby
			 */
			while(LobbysR.next()) {
				lobbys.add(new Integer[] {LobbysR.getInt(1), LobbysR.getInt(2)});
			}
			
			/*
			 * Liberamos los recursos utilizados
			 */
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
	
	/**
	 * Desconecta al usuario del lobby y en caso de que el lobby se quede vacio lo elimina de la base de datos
	 * @param id_user Id del usuario que se quiere desconectar de la sala
	 * @param id_lobby Id del lobby del que se quiere desconectar el usuario
	 */
	public void DesconectarLobby(String id_user, Integer id_lobby) { 
		Connection conn = null;
		
		try {
			/*
			 * Abrimos la conexión e inicializamos los parámetros de la sentencias para quitar al usuario
			 * del lobby y conseguir el numero de usuarios dentro de esa sala
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement deleteLobbyUser = conn.prepareStatement(deleteUserLobby);
			deleteLobbyUser.setString(1, id_user);
			deleteLobbyUser.execute();
			
			PreparedStatement lobbyvacia = conn.prepareStatement(selectUsersLobby2);
			lobbyvacia.setInt(1, id_lobby);
			ResultSet lobbyvaciaR = lobbyvacia.executeQuery();
			lobbyvaciaR.next();
			int nusers = lobbyvaciaR.getInt("nusers");	//Numero de usuarios dentro de la sala de la que se quiere desconectar
			
			if(nusers==1) {
				
				/*
				 * Si el numero de usuarios de la sala era 1, entonces la sala se habra quedado vacia sin el usuario
				 * por lo que se tendra que eliminar el lobby de la base de datos
				 */
				ManosDAO aux = new ManosDAO();
				aux.eliminarMazo(id_lobby);
				PreparedStatement delLobby = conn.prepareStatement(deleteLobby);
				delLobby.setInt(1, id_lobby);
				delLobby.execute();
				delLobby.close();
				
				
			}
			else {
				
				/*
				 * Si un quedan usuarios dentro de la sala eso implica que hay que restarle uno al numero de
				 * usuarios dentro de esta
				 */
				PreparedStatement UsersLobby = conn.prepareStatement(updateUserLobby);
				UsersLobby.setInt(1, nusers-1);
				UsersLobby.setInt(2,id_lobby);
				UsersLobby.executeUpdate();
				UsersLobby.close();
				
			}
		
			/*
			 * Liberamos los recursos utilizados restantes
			 */
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
