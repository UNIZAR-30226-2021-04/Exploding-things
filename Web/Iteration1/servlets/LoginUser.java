package servlets;

import java.io.IOException;
import java.io.PrintWriter;

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
	 * Parametros URL user=string, pass=string (Usuario y contraseña del usuario)
	 * Valida al usuario con user y pass de la URL. Si no es correcto devuelve JSON 'result: true' en caso contrario 'result:false'.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LoginUsersDAO facade = new LoginUsersDAO();		//Variable para el uso de las funciones de la clase LoginUsersDAO
		UserVO user = new UserVO(request.getParameter("user"),request.getParameter("pass"));	//Variable que representa un usuario
		String result;	//Variable que indica si se ha podido validar el usuario correctamente
		
		if(facade.validateUser(user)) {
			
			/*
			 * Se ha podido validar el usuario correctamente
			 */
			result="true";
		
		}
		else { 
		
			/*
			 * No se ha podido validar el usuario correctamente
			 */
			result="false";
		
		}
		
		/*
		 * Creacion y envio del JSON por el descriptor
		 */
		JSONObject obj = new JSONObject().put("result",result);
		
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
