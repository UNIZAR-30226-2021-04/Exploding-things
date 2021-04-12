package servlets_frontend;

import java.io.IOException;



import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class abandonar_lobby
 */
@WebServlet("/abandonar_lobby")
public class abandonar_lobby extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public abandonar_lobby() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Elimina al usuario del lobby en el que estaba y le devuleve al home.
	 * 
	 * @param id_user :Nombre del usuario
	 * @param id_lobby :Identificador de la partida
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("id_user");
		if (user==null || user.isEmpty())
		{
			response.sendRedirect("index.html");
		} else {
			request.getSession().removeAttribute("doNotServe");
			String lobby = request.getParameter("id_lobby");
			Rck_conn con = new Rck_conn();
			con.connect("Lobby?id_user=" + user + "&id_lobby=" + lobby + "&f=d");
			response.sendRedirect("buscar_partidas");
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
