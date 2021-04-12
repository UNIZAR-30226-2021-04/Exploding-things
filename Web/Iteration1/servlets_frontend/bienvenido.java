package servlets_frontend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class bienvenido
 */
@WebServlet("/bienvenido")
public class bienvenido extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public bienvenido() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Si hay un usuario en la sesión, le envía a la pantalla de home.
	 * Si no, le envía a la pantalla de login.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String)request.getSession().getAttribute("id_user");
		if (user == null || user.equals("")) 
		{
			request.getRequestDispatcher("login.jsp").forward(request,response);
		}
		else
		{
			request.getRequestDispatcher("buscar_partidas").forward(request,response);
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
