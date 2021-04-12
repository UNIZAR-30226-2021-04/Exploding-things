package servlets_frontend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class empezar_partida
 */
@WebServlet("/empezar_partida")
public class empezar_partida extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public empezar_partida() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Lleva al jugador del lobby a la partida y le da su mano de cartas. Si es el host, también le da el mazo.
	 * 
	 * @param id_user :Nombre del usuario
	 * @param id_lobby :Identificador de la partida
	 * @param host :"true" si el usuario es host de la partida, "false" si no lo es
	 * 
	 * @return id_user (Nombre del usuario) id_lobby (Identificador de la partida) host ("true" si el usuario es host de la partida, "false" si no lo es)
	 * 			mano (Lista con el nombre de las 5 cartas que componen la mano inicial) numuser (Número de usuarios de la partida) Si host es "true", mazo 
	 * 			(Lista con los nombres de las cartas de la partida)
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().removeAttribute("doNotServe");
		String user = request.getParameter("id_user");
		String lobby = request.getParameter("id_lobby");
		String host = request.getParameter("host");
		Rck_conn con = new Rck_conn();
		JSONObject obj;
		JSONArray mano, mazo;
		obj = con.connect("Partida?user=" + user + "&host=" + host + "&id_lobby=" + lobby);
		if(host.contentEquals("true")) 
		{ 
			mazo = obj.getJSONArray("mazo");
			request.setAttribute("mazo", mazo.toString());
		} 
		mano = obj.getJSONArray("mano");
		String numuser = Integer.toString(obj.getInt("numuser"));
		request.setAttribute("id_user", user);
		request.setAttribute("id_lobby", lobby);
		request.setAttribute("host", host);
		request.setAttribute("mano", mano.toString());
		request.setAttribute("numuser", numuser);
		request.getRequestDispatcher("partida.jsp").forward(request,response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
