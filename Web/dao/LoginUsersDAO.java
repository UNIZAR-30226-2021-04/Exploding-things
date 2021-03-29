package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.ConnectionManager;
import database.PoolConnectionManager;
import vo.UserVO;


/**
 * Clase encargada de la validacion de los usuarios para el login
 */
public class LoginUsersDAO {
	
	private static String countByUserName = "SELECT count(*) cuenta FROM Users WHERE Username= ?";	//Sentencia que cuenta el numero de usuarios cuyo nombre de uusario coincide con uno dado
	private static String findByUserName = "SELECT * FROM Users WHERE Username = ?";				//Sentencia que seleciona los usuarios cuyo nombre de usuario coincide con uno dado
	
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
	
}