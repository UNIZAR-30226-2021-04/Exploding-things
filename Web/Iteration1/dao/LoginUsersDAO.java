package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import database.ConnectionManager;
import database.PoolConnectionManager;
import vo.UserVO;


/**
 * Clase encargada de la validacion de los usuarios para el login
 */
public class LoginUsersDAO {
	
	private static String countByUserName = "SELECT count(*) cuenta FROM Users WHERE Username= ?";	//Sentencia que cuenta el numero de usuarios cuyo nombre de uusario coincide con uno dado
	private static String findByUserName = "SELECT * FROM Users WHERE Username = ?";//Sentencia que seleciona los usuarios cuyo nombre de usuario coincide con uno dado
	private static String findByEmail = "SELECT * FROM Users WHERE Email = ?";
	private static String dataUserLobby = "SELECT id_lobby FROM Users WHERE Username = ?";
	private static String dataUserMano = "SELECT TOP 1 id_carta FROM Mano WHERE Username = ?";
	private static String dataUserStart = "SELECT pstart FROM Lobby WHERE id_lobby = ?";
	private static String createAccount = "INSERT INTO Users (Username,Password,Email) VALUES(?,?,?)";
	private static String insertImage = "INSERT INTO ImagesUser VALUES ('None',?)";
	private static String newUserCode = "UPDATE Users SET code = ? WHERE Email=?";
	private static String selectUserCode = "SELECT code FROM Users WHERE Email=?";
	private static String newPassword = "UPDATE Users SET Password=? WHERE Email=?";
	
	/** 
	 * Valida a un usuario
	 * @param UseVO con la informacion del usuario que se quiere validar
	 * @returnObjeto True en caso de que el usuario haya podido validarse correctamente
	 */
	public boolean validateUser(UserVO user) { 
		
		boolean result = false;	//Resultado a devolver
		Connection conn = null;
		
		try {
			
			/*
			 *  Abrimos la conexión, preparamos las sentencias e inicializamos los parámetros de estas
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement countPs = conn.prepareStatement(countByUserName);
			PreparedStatement findPs = conn.prepareStatement(findByUserName);
			countPs.setString(1, user.getUserName());
			findPs.setString(1, user.getUserName());
			
			/*
			 * Ejecutamos las consultas preparadas
			 */
			ResultSet findRs = findPs.executeQuery();
			ResultSet countRs = countPs.executeQuery();
			
			/*
			 * Cojemos el numero de usuarios cuyo nombre sea el pasado como parametro
			 */
			countRs.next();
			int n = countRs.getInt(1);	//Numero de usuarios cuyo nombre coincida con el del usuario pasado como parametro
			
			
			/*
			 * En caso de que se haya encontrado 1 implica que ese usuario existe dentro de la base de datos
			 */
			if(n == 1) {
				/*
				 * Comparamos contraseñas
				 */
				findRs.next();
				String dbpwd = findRs.getString("Password");
				
				/*
				 * Si el nombre del usuario y su contrasegna coinciden entonces la validacion habra sido correcta
				 */
				if (dbpwd.contentEquals(user.getPassword())) {
					result = true;
				}
				
			} else { 
				/*
				 * En caso de que se haya encontrado mas de una coincidencia la validacion habra sido incorrecta
				 */
				result = false;  
			
			} 
			
			/*
			 * Liberamos los recursos utilizados
			 */
			findRs.close();
			findPs.close();
			countRs.close();
			countPs.close();

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
	 * Obtiene los datos requeridos para la portabilidad (id_lobby,pstart,mano)
	 * @param id_user Nombre del usuario del que se quiere saber la portabilidad
	 * @return ArrayList<Integer> con [0](id_lobby):integer, [1](pstart):0,1 [2](mano):0,1
	 */
	public ArrayList<Integer> datosPortabilidad(String id_user){
		ArrayList<Integer> datos = new ArrayList<Integer>();
		Connection conn = null;
		
		try {
			
			/*
			 * Obtenemos el id_lobby del usuario
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement lobby = conn.prepareStatement(dataUserLobby);
			lobby.setString(1,id_user);
			ResultSet lobbyRs = lobby.executeQuery();
			lobbyRs.next();
			Integer id_lobby = lobbyRs.getInt(1);
			datos.add(id_lobby);
			
			if (!id_lobby.equals(0)) {
				/*
				 * Obtenemos el pstart de id_lobby del usuario en caso de que lo tenga
				 */
				PreparedStatement start = conn.prepareStatement(dataUserStart);
				start.setInt(1,id_lobby);
				ResultSet startRs = start.executeQuery();
				startRs.next();
				datos.add(startRs.getInt(1));
				/*
				 * Comprobamos si el jugador id_user tiene mano en la lobby id_lobby
				 */
				PreparedStatement mano = conn.prepareStatement(dataUserMano);
				mano.setString(1, id_user);
				ResultSet manoRs = mano.executeQuery();
				datos.add((manoRs.next() ? 0 : 1));
				
				start.close();
				startRs.close();
				mano.close();
				manoRs.close();
			}
			else {
				datos.add(0);datos.add(0);
			}
			lobby.close();
			lobbyRs.close();
			
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
		return datos;
		}
	/**
	 * Se registra al usuario user con los parametros indicados, si es posible.
	 * @param user UserVO
	 * @return 3 si no ha salido bien, 1 si el correo ya está usado por otro usuario, 2 si el correo ya existe, 0 si se ha podido registrar
	 */
	public Integer existeUsuario(UserVO user) {
		Connection conn = null;
		Integer result = 3;
		
		try {
			
			/*
			 * Comprobamos si existe ya un usuario con ese nombre
			 */
			conn = ConnectionManager.getConnection(); 
			PreparedStatement username = conn.prepareStatement(findByUserName);
			username.setString(1, user.getUserName());
			ResultSet usernameRs=username.executeQuery();
			if (usernameRs.next()) { 
				result = 2;
			}
			else {
				/*
				 * Comprobamos si existe un usuario con ese email
				 */
				PreparedStatement email = conn.prepareStatement(findByEmail);
				email.setString(1, user.getEmail());
				ResultSet emailRs=email.executeQuery();
				if (emailRs.next()) { 
					result = 1;
				}
				else { 
					/*
					 * Creamos una cuenta al usuario
					 */
					PreparedStatement account = conn.prepareStatement(createAccount);
					account.setString(1, user.getUserName());
					account.setString(2, user.getPassword());
					account.setString(3, user.getEmail());
					account.execute();
					PreparedStatement image = conn.prepareStatement(insertImage);
					image.setString(1, user.getUserName());
					image.execute();
					result=0;
					
					account.close();
				}
				email.close();
				emailRs.close();
				
			}
			
			username.close();
			usernameRs.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		
		return result;
	}
	private String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	/**
	 * Genera un codigo aleatorio de recuperacion para el usuario con correo email
	 * @param email 
	 * @return String, codigo de recuperacion
	 */
	public String recuperarPass(String email) {
		String code = null;
		Connection conn = null;
		try {
			/*
			 * Generamos codigo aleatorio
			 */
			Random r = new Random();
			code = "";
			for (int i = 0;i<4;i++) {
				code += characters.charAt(r.nextInt(characters.length()));
			}
			conn = ConnectionManager.getConnection();
			PreparedStatement newcode = conn.prepareStatement(newUserCode);
			newcode.setString(1,code);
			newcode.setString(2,email);
			if (newcode.executeUpdate() <= 0) {
				code = null;
			}
			newcode.close();
			
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		return code;
	}
	/**
	 * Comprueba si el codigo de recuperacion code es correcto 
	 * @param code Codigo de recuperacion
	 * @param email Usuario que recupera la contraseña
	 * @return Boolean, true si el codigo coincide
	 */
	public Boolean codigoRecuperacion(String code,String email) {
		Boolean result = false;
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			PreparedStatement vcode = conn.prepareStatement(selectUserCode);
			vcode.setString(1,email);
			ResultSet vcodeRs = vcode.executeQuery();
			vcodeRs.next();
			result = code.equals(vcodeRs.getString(1));
			vcode.close();
			vcodeRs.close();
			
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
	 * Cambia la contraseña del usuario con correo email
	 * @param pass Nueva contraseña
	 * @param email correo electronica
	 * @return Devuelve true si se ha podido cambiar la contraseña correctamente
	 */
	public Boolean newPass(String pass, String email) {
		Boolean result = false;
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			PreparedStatement np = conn.prepareStatement(newPassword);
			np.setString(1, pass);
			np.setString(2, email);
			result = np.executeUpdate() > 0;
			np.close();
			
		} catch(SQLException se) {
			
			se.printStackTrace();  
		
		} catch(Exception e) {
			
			e.printStackTrace(System.err); 
			
		} finally {
			
			PoolConnectionManager.releaseConnection(conn); 
			
		}
		return result;
	}
		
}