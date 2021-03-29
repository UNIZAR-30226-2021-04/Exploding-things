package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import database.ConnectionManager;
import database.PoolConnectionManager;

public class ManosDAO {
	
	private static String insertarCartas = "INSERT INTO Mazo VALUES(?,?,?)";								//Sentencia para la inserción de cartas en la tabla Mazo
	private static String actualizarMazo_1 = "UPDATE Mazo SET num=num+1 WHERE id_lobby=? AND id_carta=?";	//Sentencia para agnadir una unidad del tipo de carta en la tabla Mazo
	private static String actualizarMazo_2 = "UPDATE Mazo SET num=num-1 WHERE id_lobby=? AND id_carta=?";	//Sentencia para quitar una unidad del tipo de carta en la tabla Mazo
	private static String insertarMano = "INSERT INTO Mano VALUES(?,?,?)";									//Sentencia para la inserción de cartas en la tabla Mano
	private static String obtenerMazo = "SELECT id_carta,num FROM Mazo WHERE id_lobby=?";					//Sentencia que devuelve el mazo de un lobby
	private static String actualizarMano_1 = "UPDATE Mano SET num=num+1 WHERE Username=? AND id_carta=?";	//Sentencia para agnadir una unidad del tipo de carta en la tabla Mano
	private static String actualizarMano_2 = "UPDATE Mano SET num=num-1 WHERE Username=? AND id_carta=?";
	private static String obtenerMano = "SELECT id_carta,num FROM Mano WHERE Username=?";					//Sentencia que devuelve la mano de un usuario
	private static String empezarPartida = "UPDATE Lobby SET pstart=1 WHERE id_lobby=?";					//Sentencia que actualiza la base de datos para indicar que la partida ha comenzado
	private static String actualizarMazo_t = "UPDATE Mazo SET num=num+? WHERE id_lobby=? AND id_carta=?";	//Sentencia que agnade unidades de cartas al mazo de un lobby
	private static String deleteMano = "DELETE FROM Mano WHERE Username=?";	//Sentencia para la eliminacion completa de la mano de usuario
	private static String eliminarMazo = "DELETE FROM Mazo WHERE id_lobby=?";	//Sentencia para la eliminacion completa del mazo							
	private static String seleccionarUsuarios = "SELECT nusers FROM Lobby WHERE id_lobby=?";
	private static String deleteLobbyJugador = "UPDATE Users SET id_lobby=NULL WHERE Username=?";
	private static String selectBombas = "SELECT num FROM Mano WHERE Username=? AND id_carta='Desastre'";
	/**
	 * Metodo que crea en la base de datos el mazo del lobby
	 * @param id_lobby Id del lobby en el que se quiere crear el mazo
	 */
	public void crearMazo (Integer id_lobby){ 
		Connection conn = null;

		try {
			/*
			 * Abrimos la conexión e inicializamos el para metro comun de las querys
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement nuevoMazo = conn.prepareStatement(insertarCartas);
			nuevoMazo.setInt(1, id_lobby);
			
			/*
			 * Insertar cartas de barajar
			 */
			nuevoMazo.setString(2,"Barajar");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			/*
			 * Insertar cartas desastre
			 */
			nuevoMazo.setString(2,"Desastre");
			nuevoMazo.setInt(3,-1);
			nuevoMazo.execute();
			
			/*
			 * Insertar cartas escape
			 */
			nuevoMazo.setString(2,"Escape");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			/*
			 * Insertar cartas eureka
			 */
			nuevoMazo.setString(2,"Eureka");
			nuevoMazo.setInt(3,5);
			nuevoMazo.execute();
			
			/*
			 * Insertar cartas marciano
			 */
			nuevoMazo.setString(2,"Marciano1");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			nuevoMazo.setString(2,"Marciano2");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			nuevoMazo.setString(2,"Marciano3");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			nuevoMazo.setString(2,"Marciano4");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			/*
			 * Insertar cartas robar
			 */
			nuevoMazo.setString(2,"Robar");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			/*
			 * Insertar cartas sabotaje 
			 */
			nuevoMazo.setString(2,"Sabotaje");
			nuevoMazo.setInt(3,4);
			nuevoMazo.execute();
			
			/*
			 * Insertar cartas salvacion
			 */
			nuevoMazo.setString(2,"Salvacion");
			nuevoMazo.setInt(3,6);
			nuevoMazo.execute();

			/*
			 * Liberamos los recursos utilizados
			 */
			nuevoMazo.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
	}

	/**
	 * Metodo que crea la mano de un usuario dentro del lobby con id id_lobby
	 * @param id_lobby Id del lobby en el que se encuentra el usuario
	 * @param Username Nombre del usuario del que se quiere crear la mano
	 */
	public void crearMano (Integer id_lobby,String Username){ 
		Connection conn = null;
		ArrayList<String> cartas = new ArrayList<String>();	//Array de las cartas que se van a meten en la mano de un usuario
		
		try {
			
			/*
			 * Abrimos la conexión e inicializamos los parámetros comunes de cada una de las consultas preparadas
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement crearMano = conn.prepareStatement(insertarMano);
			PreparedStatement anyadirCartaMazo = conn.prepareStatement(actualizarMazo_1);
			PreparedStatement eliminarCarta = conn.prepareStatement(actualizarMazo_2);
			PreparedStatement seleccionMazo = conn.prepareStatement(obtenerMazo);
			seleccionMazo.setInt(1,id_lobby);
			
			/*
			 * Anyadiamos una carta bomba al mazo
			 */
			anyadirCartaMazo.setInt(1,id_lobby); 
			anyadirCartaMazo.setString(2,"Desastre"); 
			anyadirCartaMazo.execute();
			
			/*
			 * Eliminamos una carta de salvacion del mazo
			 */
			eliminarCarta.setInt(1,id_lobby); 
			eliminarCarta.setString(2,"Salvacion"); 
			eliminarCarta.execute();
			
			/*
			 * Insertar la carta de salvacion del mazo y 0 del resto
			 */
			crearMano.setString(1,Username);
			
			crearMano.setString(2,"Salvacion");
			crearMano.setInt(3,1); 
			crearMano.execute();
			
			crearMano.setString(2, "Barajar");
			crearMano.setInt(3,0);crearMano.execute();
			crearMano.setString(2, "Escape");
			crearMano.setInt(3,0);crearMano.execute();
			crearMano.setString(2, "Eureka");
			crearMano.setInt(3,0);crearMano.execute();
			
			crearMano.setString(2, "Marciano1");
			crearMano.setInt(3,0);
			crearMano.execute();
			
			crearMano.setString(2, "Marciano2");
			crearMano.setInt(3,0);
			crearMano.execute();
			
			crearMano.setString(2, "Marciano3");
			crearMano.setInt(3,0);
			crearMano.execute();
			
			crearMano.setString(2, "Marciano4");
			crearMano.setInt(3,0);
			crearMano.execute();
			
			crearMano.setString(2, "Robar");
			crearMano.setInt(3,0);
			crearMano.execute();
			
			crearMano.setString(2, "Sabotaje");
			crearMano.setInt(3,0);
			crearMano.execute();
			
			/*
			 * Agnadimos al array las cartas disponibles del mazo
			 */
			ResultSet seleccionMazoRs = seleccionMazo.executeQuery();
			
			while(seleccionMazoRs.next()) {
				
				for(int i=0;i<seleccionMazoRs.getInt(2);i++) {
					
					cartas.add(seleccionMazoRs.getString(1));
					
				}
				
			}
			
			/*
			 * Barajamos las cartas
			 */
			Collections.shuffle(cartas,new Random());
			
			/*
			 * Preparamos la sentencia para agnadir cartas en la mano de un usuario
			 */
			PreparedStatement anyadirCartaMano = conn.prepareStatement(actualizarMano_1); 
			anyadirCartaMano.setString(1,Username);
			
			Integer ncartas = 0;	//Variable que indica el numero de cartas introducidas a la mano del usuario
			
			for(int i = 0;i < cartas.size() && ncartas < 4;i++) {
				
				String nuevacarta = (String) cartas.get(i);	//Id de la carta a introducir
				
				/*
				 * En caso de que la carta no sea de desastre ni de salvacion se puede pasar del mazo del lobby a
				 * la mano del usuario
				 */
				if ( !nuevacarta.equals("Desastre") && !nuevacarta.equals("Salvacion") ) {
					
					/*
					 * Anyado carta al usuario
					 */
					anyadirCartaMano.setString(2,cartas.get(i));
					anyadirCartaMano.execute();
					
					/*
					 * Elimino carta del mazo
					 */
					eliminarCarta.setString(2,cartas.get(i));
					eliminarCarta.execute();
					
					ncartas++;	
				}
			}

			/*
			 * Liberamos los recursos utilizados
			 */
			crearMano.close();
			anyadirCartaMazo.close();
			eliminarCarta.close();
			seleccionMazo.close();
			seleccionMazoRs.close();
			anyadirCartaMano.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
	}
	
	/**
	 * Clase que representa un tipo de carta y el numero de estas dentro del mazo
	 */
	public class Mazo {
		
		String id_carta;	//Id de la carta
		Integer num;		//Numero de estas dentro del mazo
		
		public Mazo(String id_carta, Integer num) {
			this.id_carta = id_carta;
			this.num = num;
		}
		
		/**
		 * Get del atributo id_carta
		 * @return Id de la carta que representa
		 */
		public String getId_carta() {
			return id_carta;
		}
		
		/**
		 * Set del atributo id_carta
		 * @param id_carta Nuevo id de la carta
		 */
		public void setId_carta(String id_carta) {
			this.id_carta = id_carta;
		}
		
		/**
		 * Get del atributo num
		 * @return Cantidad de cartas con id id_carta dentro del mazo
		 */
		public Integer getNum() {
			return num;
		}
		
		/**
		 * Set del atributo num
		 * @param num Nueva cantidad de cartas con id id_carta
		 */
		public void setNum(Integer num) {
			this.num = num;
		}
		
	}
	
	/**
	 * Metodo que busca la mano de usuario
	 * @param Username Nombre del usuario del que se quiere buscar la mano
	 * @return Array con la mano actual del usuario
	 */
	public ArrayList<Mazo> devolverMano (String Username) { 
		
		Connection conn = null;
		ArrayList<Mazo> cartas = new ArrayList<Mazo>();	//Array a devolver
		
		try {
			
			/*
			 * Abrimos la conexion y nos preparamos para obtener la mano del usuario
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement seleccionaMano = conn.prepareStatement(obtenerMano);
			seleccionaMano.setString(1, Username);
			
			/*
			 * Bucle que ejecuta la busqueda y agnade al array el tipo y la cantidad de cada tipo de carta que
			 * el usuario posee en su mano
			 */
			ResultSet seleccionaManoRs = seleccionaMano.executeQuery();
			while(seleccionaManoRs.next()) {
				
				cartas.add(new Mazo(seleccionaManoRs.getString(1),seleccionaManoRs.getInt(2)));
				
			}
			
			/*
			 * Liberamos los recursos utilizados
			 */
			seleccionaMano.close();
			seleccionaManoRs.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
		return cartas;
		
	}
	
	/**
	 * Metodo que actualiza la base de datos para que quede reflejado que las personas del lobby con id id_lobby
	 * han comenzado a jugar
	 * @param id_lobby Id del lobby que se quiere empezar
	 */
	public void comenzarPartida (Integer id_lobby) { 
		
		Connection conn = null;
		
		try {
			
			/*
			 * Abrimos la conexion y ejecutamos la consulta para actualizar la base de datos
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement start = conn.prepareStatement(empezarPartida);
			start.setInt(1, id_lobby);
			start.execute();
			
			/*
			 * Liberamos los recursos utilizados
			 */
			start.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
		}
		
	}
	/**
	 * Metodo que devuelve la mano del jugador id_user al mazo de id_lobby
	 * @param id_user Nombre del usuario del que se devuelve la mano
	 * @param id_lobby Id del lobby al que se devuelve la mano
	 */
	public void returnMazo (String id_user, Integer id_lobby){ 
		Connection conn = null;

		try {
			
			/*
			 * Abrimos la conexion y preparamos las sentencias a ejecutar
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement updateMazo = conn.prepareStatement(actualizarMazo_t);
			PreparedStatement anyadirDesastre = conn.prepareStatement(actualizarMazo_2);
			PreparedStatement getMano = conn.prepareStatement(obtenerMano);
			PreparedStatement eliminarMano = conn.prepareStatement(deleteMano);
			
			/*
			 * Metemos en las consultas los parametros comunes
			 */
			eliminarMano.setString(1, id_user);
			getMano.setString(1, id_user);
			updateMazo.setInt(2, id_lobby);
			
			/*
			 * Bucle que coje la mano del usuario y la agnade al mazo del lobby las cartas
			 */
			ResultSet getManoRs = getMano.executeQuery();
			while(getManoRs.next()) {
				
				updateMazo.setInt(1, getManoRs.getInt(2));
				updateMazo.setString(3, getManoRs.getString(1));
				updateMazo.execute();
				
			}
			anyadirDesastre.setInt(1, id_lobby);
			anyadirDesastre.setString(2, "Desastre");
			anyadirDesastre.execute();
			/*
			 * Eliminamos de la base de datos la mano del jugador
			 */
			eliminarMano.execute();
			
			/*
			 * Liberamos los recursos utilizados
			 */
			getMano.close();
			getManoRs.close();
			updateMazo.close();
			eliminarMano.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
	}
	/**
	 * Metodo que elimina el mazo del lobby id_lobby
	 * @param id_lobby Id del lobby al que se le elimina el mazo
	 */
	public void eliminarMazo (Integer id_lobby){ 
		Connection conn = null;

		try {
			
			/*
			 * Abrimos la conexion y preparamos las sentencias a ejecutar
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement deleteMazo = conn.prepareStatement(eliminarMazo);
			deleteMazo.setInt(1, id_lobby);
			deleteMazo.execute();
			deleteMazo.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
	}
	/**
	 * Metodo que devuelve el mazo de la lobby id_lobby
	 * @param id_lobby Id del lobby del que se devuelve el mazo
	 * @return Array de strings con el nombre de las cartas del mazo
	 */
	public ArrayList<String> devolverMazo (Integer id_lobby) { 
		
		Connection conn = null;
		ArrayList<String> cartas = new ArrayList<String>();	//Array a devolver
		
		try {
			
			/*
			 * Abrimos la conexion y nos preparamos para obtener la mano del usuario
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement seleccionaMazo = conn.prepareStatement(obtenerMazo);
			seleccionaMazo.setInt(1, id_lobby);
			
			/*
			 * Bucle que ejecuta la busqueda y agnade al array el nombre de la carta del mazo
			 */
			ResultSet seleccionaMazoRs = seleccionaMazo.executeQuery();
			while(seleccionaMazoRs.next()) {
				
				for(int i=0;i<seleccionaMazoRs.getInt(2);i++) {
					
					cartas.add(seleccionaMazoRs.getString(1));
					
				}
				
			}
			Collections.shuffle(cartas,new Random());
			
			/*
			 * Liberamos los recursos utilizados
			 */
			seleccionaMazo.close();
			seleccionaMazoRs.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
		return cartas;
		
	}
	/**
	 * Metodo que devuelve el numero de usuario de la lobby id_lobby
	 * @param id_lobby Id del lobby del que se devuelve el num usuarios
	 * @return numero de usuarios de la lobby id_lobby
	 */
	public Integer numUsuarios (Integer id_lobby) {
		Connection conn = null;
		Integer numuser = 0;
		
		try {
			
			/*
			 * Abrimos la conexion y seleccionamos la lobby 
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement obtenerUsuarios = conn.prepareStatement(seleccionarUsuarios);
			obtenerUsuarios.setInt(1, id_lobby);
			
			/*
			 * Ejecutamos la operacion select
			 */
			ResultSet obtenerUsuariosRs = obtenerUsuarios.executeQuery();
			obtenerUsuariosRs.next();
			numuser = obtenerUsuariosRs.getInt(1);
			
			/*
			 * Liberamos los recursos utilizados
			 */
			obtenerUsuarios.close();
			obtenerUsuariosRs.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
		return numuser;
	}
	/**
	 * Metodo que modifica la mano de un jugador (si f=anyadir anyade la carta id_carta a la mano de id_user,
	 * si no, elimina la carta id_carta de la mano de id_user)
	 * @param id_user Nombre del jugador al que se le modifica la mano
	 * @param id_carta Nombre de la carta que se va a modificar
	 * @param f Operacion a realizar ("anyadir" o "eliminar")
	 */
	public void modificarMano(String id_user,String id_carta,String f) {
		
		Connection conn = null;
		
		try {
			/*
			 * Preparas la sentencia a ejecutar en funcion del parametro f
			 */
			conn = ConnectionManager.getConnection();
			PreparedStatement actualizarMano;
			if (f.equals("anyadir")) {
				actualizarMano = conn.prepareStatement(actualizarMano_1);
			}
			else { actualizarMano = conn.prepareStatement(actualizarMano_2); }
			/*
			 * Ejecutas la sentencia (anyades o eliminas la carta)
			 */
			actualizarMano.setString(1, id_user);
			actualizarMano.setString(2, id_carta);
			actualizarMano.execute();
			actualizarMano.close();
			
	
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
	}
	/**
	 * Metodo que elimina la mano de un jugador id_user
	 * @param id_user Nombre del jugador al que se le elimina la mano
	 */
	public void eliminarMano(String id_user) {
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			PreparedStatement eliminar = conn.prepareStatement(deleteMano);
			eliminar.setString(1,id_user);
			eliminar.execute();
			eliminar.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}	
	}
	/**
	 * Metodo que elimina la lobby asociada al jugador
	 * @param id_user Nombre del jugador al que se le elimina la lobby
	 */
	public void eliminarLobbyJugador(String id_user) {
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			PreparedStatement eliminarL = conn.prepareStatement(deleteLobbyJugador);
			eliminarL.setString(1,id_user);
			eliminarL.execute();
			eliminarL.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}	
	}
	/**
	 * Metodo que obtiene el num cartas de desastre del jugador id_user
	 * @param id_user Nombre del jugador
	 */
	public Integer numBombas(String id_user) {
		Connection conn = null;
		Integer bombas = 0;
		try {
			conn = ConnectionManager.getConnection();
			PreparedStatement bomba = conn.prepareStatement(selectBombas);
			bomba.setString(1,id_user);
			ResultSet bombaRs = bomba.executeQuery();
			bombaRs.next();
			bombas = bombaRs.getInt(1);
			
			bombaRs.close();
			bomba.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		return bombas;
	}
}