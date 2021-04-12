package servlets_frontend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class signup
 */
@WebServlet("/signup")
public class signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public signup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Permite al usuario registrarse. Si el usuario es registrado con éxito, redirige al home. Si no, muestra un error.
	 * 
	 * @param user :Nombre del usuario
	 * @param mail :Correo electrónico de la cuenta
	 * @param pass :Contraseña del usuario
	 * 
	 * @return id_user (Nombre del usuario) si el login tiene éxito
	 * 		   error (Mensaje de error) si el login falla
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
		String mail = request.getParameter("mail");
		String pass = request.getParameter("pass");
		JSONObject obj = new Rck_conn().connect("LoginUser?user=" + user + "&mail=" + mail + "&pass=" + pass);
		boolean result = obj.getBoolean("result");
		if (result)
		{
			request.getSession().setAttribute("id_user", user);
			request.getRequestDispatcher("home.jsp").forward(request,response);
		} else 
		{
			request.setAttribute("error", "El usuario ya existe");
			request.getRequestDispatcher("signup.jsp").forward(request,response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
