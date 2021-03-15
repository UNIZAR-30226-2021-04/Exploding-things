package database;

import java.sql.*;

/**  * Clase que abstrae la conexion con la base de datos.  */

public class ConnectionManager {
	private static final String DB_URL = "jdbc:sqlserver://rocketruckus.database.windows.net:1433;database=RocketRuckusDB;user=margareth@rocketruckus;password=Hamilton10;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
		
	// Devuelve una nueva conexion.
	public final static Connection getConnection() throws SQLException {
		Connection conn = null;

		try{
			//STEP 1: Register JDBC driver
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			//STEP 2: Open a connection
			conn = DriverManager.getConnection(DB_URL);
			return conn; 
		} catch (Exception e) {
			e.printStackTrace();
			return null; 
		} 
	}
	
	// Libera la conexion, devolviendola al pool 
	public final static void releaseConnection(Connection conn) throws SQLException {
		conn.close(); 
	}
}

