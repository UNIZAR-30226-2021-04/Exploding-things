package servlets_frontend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class cambiar_foto
 */
@WebServlet("/cambiar_foto")
public class cambiar_foto extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public cambiar_foto() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("id_user");
		String foto = request.getParameter("foto");
		
		Rck_conn con = new Rck_conn();
		JSONObject obj = con.connect("Perfil?id_user=" + user + "&image=" + foto + "&f=i");
		boolean result = obj.getBoolean("result");
		if (result)
		{
			request.getSession().setAttribute("foto", foto);
		}
		else
		{
			request.getSession().setAttribute("error","No se ha podido realizar el cambio");
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
