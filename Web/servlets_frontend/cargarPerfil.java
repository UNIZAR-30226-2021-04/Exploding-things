package servlets_frontend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class cargarPerfil
 */
@WebServlet("/cargarPerfil")
public class cargarPerfil extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public cargarPerfil() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("id_user");
		String error = (String) request.getSession().getAttribute("error");
		request.getSession().removeAttribute("error");
		if (user==null || user.contentEquals(""))
		{
			response.sendRedirect("index.html");
		}
		
		Rck_conn con = new Rck_conn();
		JSONObject obj = con.connect("Perfil?id_user=" + user + "&f=v");
		String logros = obj.getJSONArray("logros").toString();
		String fotos = obj.getJSONArray("fotos").toString();
		int pjugadas = obj.getInt("pjugadas");
		int pganadas = obj.getInt("pganadas");
		
		obj = con.connect("Amigos?user=" + user + "&f=v");
		String solicitudes = obj.getJSONArray("solicitud").toString();
		String amigos = obj.getJSONArray("amigos").toString();
		
		request.setAttribute("logros", logros);
		request.setAttribute("fotos", fotos);
		request.setAttribute("pjugadas", pjugadas);
		request.setAttribute("pganadas", pganadas);
		request.setAttribute("solicitudes", solicitudes);
		request.setAttribute("amigos", amigos);
		
		request.setAttribute("error", error);
		request.getRequestDispatcher("perfil.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
