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
		String cpass = request.getParameter("cpass");
		if (!pass.contentEquals(cpass))
		{
			request.setAttribute("error", "Las contraseñas no coinciden");
			request.getRequestDispatcher("signup.jsp").forward(request,response);
		} else {
			boolean todoenorden = true;
			if (user == null || !user.matches("^[a-zA-Z0-9]*$")) { todoenorden = false; }
			if (mail == null || !mail.matches("^[a-zA-Z0-9@.]*$")) { todoenorden = false; }
			if (pass == null || !pass.matches("^[a-zA-Z0-9]*$")) { todoenorden = false; }
			if (todoenorden) 
			{
				JSONObject obj = new Rck_conn().connect("LoginUser?user=" + user + "&email=" + mail + "&pass=" + pass);
				String result = obj.getString("result");
				if (result.contentEquals("true"))
				{
					request.getSession().setAttribute("id_user", user);
					request.getRequestDispatcher("home.jsp").forward(request,response);
				} else 
				{
					request.setAttribute("error", result);
					request.getRequestDispatcher("signup.jsp").forward(request,response);
				}
			} else {
				request.setAttribute("error", "Solo se permiten caracteres alfanuméricos");
				request.getRequestDispatcher("signup.jsp").forward(request,response);
			}
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
