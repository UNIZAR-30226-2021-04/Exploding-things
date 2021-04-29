package servlets_frontend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

/**
 * Servlet implementation class buscar_partidas
 */
@WebServlet("/buscar_partidas")
public class buscar_partidas extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * Devuelve la lista de los lobbies disponibles y el número de jugadores que hay en ellos.
     * Lobbies disponibles son aquellos con al menos 1 jugador y menos de 4.
	 * 
	 * @return lobby_list (array con todos los lobbies disponibles)
	 * 
     * @see HttpServlet#HttpServlet()
     */
    public buscar_partidas() {
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
		} else {
		
			String err = request.getParameter("error");
			if(err!=null&&err.equals("kick")) {
				request.setAttribute("error", "Has sido expulsado de la partida");
			}
			Rck_conn con = new Rck_conn();
			JSONArray arr = con.connectArray("Lobby");
			request.setAttribute("lobby_list",arr.toString());
			request.getRequestDispatcher("home.jsp").forward(request,response);
		
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
