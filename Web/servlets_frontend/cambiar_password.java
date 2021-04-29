package servlets_frontend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class cambiar_password
 */
@WebServlet("/cambiar_password")
public class cambiar_password extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public cambiar_password() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("id_user");
		String pass = request.getParameter("pass");
		String cpass = request.getParameter("cpass");
		
		if (pass == null || !pass.matches("^[a-zA-Z0-9]*$")) 
		{ 
			request.getSession().setAttribute("error", "Contraseña inválida");
		}
		else
		{
			if (cpass == null || !pass.contentEquals(cpass))
			{
				request.setAttribute("error", "Las contraseñas no coinciden");
			}
			else
			{
				Rck_conn con = new Rck_conn();
				JSONObject obj = con.connect("Perfil?id_user=" + user + "&pass=" + pass + "&f=p");
				boolean result = obj.getBoolean("result");
				if (!result)
				{
					request.getSession().setAttribute("error","No se ha podido realizar el cambio");
				}
			}
		}
		response.sendRedirect("cargarPerfil");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
