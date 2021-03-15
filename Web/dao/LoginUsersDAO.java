package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.ConnectionManager;
import database.PoolConnectionManager;
import vo.UserVO;

public class LoginUsersDAO {
	
	private static String countByUserName = "SELECT count(*) cuenta FROM Users WHERE Username= ?";
	private static String findByUserName = "SELECT * FROM Users WHERE Username = ?";
	
	
	/** * Busca un registro en la tabla DEMO por ID * 
		@param id Identificador del registro buscado * 
		@returnObjeto DemoVO con el identificador buscado, o null si no seencuentra 
	*/
	public boolean validateUser(UserVO user) { 
		boolean result = false;
		Connection conn = null;
		
		
		try {
			// Abrimos la conexión e inicializamos los parámetros 
			conn = ConnectionManager.getConnection(); 
			PreparedStatement countPs = conn.prepareStatement(countByUserName);
			PreparedStatement findPs = conn.prepareStatement(findByUserName);
			countPs.setString(1, user.getUserName());
			findPs.setString(1, user.getUserName());
			
			// Ejecutamos la consulta 
			ResultSet findRs = findPs.executeQuery();
			ResultSet countRs = countPs.executeQuery();
			
			countRs.next();
			int n = countRs.getInt(1);
			
			
			// Leemos resultados 
			if(n == 1) {
				// Comparamos contraseñas
				findRs.next();
				String dbpwd = findRs.getString("Password");
				if (dbpwd.contentEquals(user.getPassword())) {
					result = true;
				}
			} else { 
				result = false;  
			} 
			
			// liberamos los recursos utilizados
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