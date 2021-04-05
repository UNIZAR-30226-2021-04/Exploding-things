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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("id_user");
		String lobby = request.getParameter("id_lobby");
		Rck_conn con = new Rck_conn();
		JSONObject obj = con.connect("Lobby?id_user=" + user + "&id_lobby=" + lobby + "&f=u");
		boolean result = obj.getBoolean("result");
		if (result) 
		{
			request.setAttribute("id_user", user);
			request.setAttribute("id_lobby", lobby);
			request.getRequestDispatcher("lobby.jsp").forward(request,response);
		} else {
			request.setAttribute("error", "El lobby está lleno");
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
