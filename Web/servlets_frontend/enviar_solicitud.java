package servlets_frontend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class enviar_solicitud
 */
@WebServlet("/enviar_solicitud")
public class enviar_solicitud extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public enviar_solicitud() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("id_user");
		user = user.toUpperCase();
		String amigo = request.getParameter("amigo");
		amigo = amigo.toUpperCase();
		
		Rck_conn con = new Rck_conn();
		JSONObject obj = con.connect("Amigos?user=" + user + "&user_d=" + amigo + "&f=s");
		boolean result = obj.getBoolean("result");
		if(!result)
		{
			request.getSession().setAttribute("error", "Error al enviar la solicitud");
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
