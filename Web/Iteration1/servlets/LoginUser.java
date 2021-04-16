package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.LoginUsersDAO;
import vo.UserVO;
import org.json.*;

/**
 * Servlet para el login de los usuarios
 */
@WebServlet("/LoginUser")
public class LoginUser extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/** 
	 * Parametros URL user=string, pass=string (Usuario y contraseña del usuario) email=string (null si login, sino registro)
	 * Si login: Valida al usuario con user y pass de la URL. 
	 * 				Si es correcto devuelve JSON 'result: true id_lobby: (0,int>0) pstart:(0 lobby,1 partida) muerte:(0 no ha perdido,1 ha perdido)
	 * 				en caso contrario 'result:false'.
	 * Si registro: Devuelve 'result:"true"' si se ha podido registrar, en caso contrario devuelve un mensaje de error (string)
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LoginUsersDAO facade = new LoginUsersDAO();		//Variable para el uso de las funciones de la clase LoginUsersDAO
		UserVO user = new UserVO(request.getParameter("user"),request.getParameter("pass"),request.getParameter("email"));	//Variable que representa un usuario
		String result ="";	//Variable que indica si se ha podido validar el usuario correctamente
		JSONObject obj = new JSONObject();
		if (user.getEmail()==null) { //Login
			if(facade.validateUser(user)) {
				
				/*
				 * Se ha podido validar el usuario correctamente y anyadiamos datos portabilidad
				 */
				result="true";
				ArrayList<Integer> datos = facade.datosPortabilidad(request.getParameter("user"));
				obj.put("id_lobby",datos.get(0));
				obj.put("pstart",datos.get(1));
				obj.put("muerte",datos.get(2));
			
			}
			else { 
			
				/*
				 * No se ha podido validar el usuario correctamente
				 */
				result="false";
			
			}
		}
		else { //Registro
			switch (facade.existeUsuario(user)) {
			case 0:
				result="true";
				break;
			case 1:
				result="El correo ya existe";
				break;
			case 2:
				result="El nombre de usuario ya existe";
				break;
			case 3: 
				result="Vuelva a intentarlo";
				break;
			}
		}
		
		
		/*
		 * Creacion y envio del JSON por el descriptor
		 */
		
		obj.put("result", result);
		PrintWriter out = response.getWriter();
		out.print(obj.toString());
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
