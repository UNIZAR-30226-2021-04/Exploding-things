package servlets_frontend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class crear_partida
 */
@WebServlet("/crear_partida")
public class crear_partida extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * Crea un lobby en el que únicamente se encuentra el usuario. Devuelve una variable "host" que indica si es el host de la sala o no.
     * En este caso, siempre devuelve la variable "host" con valor true.
	 * 
	 * @param id_user :Nombre del usuario
	 * 
	 * @return id_user (Nombre del usuario) id_lobby (Identificador de la partida) host ("true" si el usuario es host de la partida, "false" si no lo es)
	 * 
     * @see HttpServlet#HttpServlet()
     */
    public crear_partida() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("id_user");
		if (user==null || user.isEmpty())
		{
			response.sendRedirect("index.html");
		}
		else
		{
			String doNotServe = (String) request.getSession().getAttribute("doNotServe");
			if (doNotServe==null) {
				Rck_conn con = new Rck_conn();
				JSONObject obj = con.connect("Lobby?id_user=" + user);
				String id_lobby = Integer.toString(obj.getInt("id_lobby"));
				request.setAttribute("host", "true");
				request.setAttribute("id_user", user);
				request.setAttribute("id_lobby", id_lobby);
				request.getSession().setAttribute("doNotServe", user);
				request.getRequestDispatcher("lobby.jsp").forward(request,response);
			} else {
				request.getSession().removeAttribute("doNotServe");
				response.sendRedirect("buscar_partidas");
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
