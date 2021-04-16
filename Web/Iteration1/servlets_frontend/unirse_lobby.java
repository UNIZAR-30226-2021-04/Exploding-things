package servlets_frontend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class unirse_lobby
 */
@WebServlet("/unirse_lobby")
public class unirse_lobby extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public unirse_lobby() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Mete al usuario dentro del lobby seleccionado, si es posible hacerlo. Si el lobby está lleno, devuelve un error.
	 * La variable "host" indica si el usuario es host de la sala. En este caso, siempre devuelve "host" con valor false.
	 * 
	 * @param id_user :Nombre del usuario
	 * @param id_lobby :Identificador de la partida
	 * 
	 * @return id_user (Nombre del usuario) id_lobby (Identificador de la partida) host ("true" si el usuario es host de la partida, "false" si no lo es)
	 * 			si se introduce al usuario en el lobby con éxito
	 * 			error (Mensaje de error) si el lobby está lleno.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("id_user");
		if(user==null||user.isEmpty()) {
			response.sendRedirect("index.html");
		} else {
			String doNotServe = (String) request.getSession().getAttribute("doNotServe");
			if (doNotServe==null) {
				String lobby = request.getParameter("id_lobby");
				Rck_conn con = new Rck_conn();
				JSONObject obj = con.connect("Lobby?id_user=" + user + "&id_lobby=" + lobby + "&f=u" );
				boolean result = obj.getBoolean("result");
				if (result) 
				{
					request.getSession().setAttribute("doNotServe", user);
					request.setAttribute("host", "false");
					request.setAttribute("id_user", user);
					request.setAttribute("id_lobby", lobby);
					request.getRequestDispatcher("lobby.jsp").forward(request,response);
				} else {
					request.setAttribute("error", "El lobby está lleno");
					request.getRequestDispatcher("buscar_partidas").forward(request,response);
				}
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
