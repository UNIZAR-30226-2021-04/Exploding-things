package vo;

public class UserVO {
	
	private String userName;	//Nombre del usuario
	private String password;	//Contrasegna de este

	/**
	 * Constructor de la clase
	 * @param userName Nombre del usuario
	 * @param password Contrasegna del usuario
	 */
	public UserVO(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	/**
	 * Metodo para el acceso al nombre de usuario
	 * @return El nombre del usuario
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Cambia el nombre de usuario al pasado como parametro
	 * @param userName Nuevo nombre del usuario
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Metodo para el acceso a la contrasegna del usuario
	 * @return La contrasegna del usuario
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Cambia la contrasegna del usuario a la pasado como parametro
	 * @param password Nueva contrasegna
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
}