package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.LobbyDAO;
import org.json.*;

/**
 * Servlet implementation class LoginAdmin
 */
@WebServlet("/Lobby")
public class Lobby extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Lobby() {
        super();
        // TODO Auto-generated constructor stub
    }

	/** 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		LobbyDAO facade = new LobbyDAO();
		PrintWriter out = response.getWriter();
		
		if (request.getParameter("id_user")== null) { //Refresh
			JSONArray obj = new JSONArray();
			ArrayList<Integer[]> lobbys = facade.SeleccionarLobbys();
			for (int i=0;i<lobbys.size();i++) {
				JSONObject aux = new JSONObject();
				aux.put("nuser", lobbys.get(i)[1]);
				aux.put("id_lobby", lobbys.get(i)[0]);
				obj.put(aux);
			}
			out.print(obj.toString());
			out.flush();
			
		}
		else {
			JSONObject obj = new JSONObject();
			String id_user= request.getParameter("id_user");
			if (request.getParameter("id_lobby")==null) { //Crear partida
				Integer id_lobby = facade.CrearLobby(id_user);
				obj.put("id_lobby",id_lobby);
			}
			else {
				Integer id_lobby = Integer.parseInt(request.getParameter("id_lobby"));
				String f = request.getParameter("f");
				switch (f) { 
					case "d"://Salir del lobby
						facade.DesconectarLobby(id_user, id_lobby);
						break;
					case "u": //Unirse al lobby
						boolean result = facade.UnirLobby(id_user, id_lobby);
						obj.put("result",result); //True si ha podido unirse, false si no ha podido
						break;
					
				}
				
			}
			
			out.print(obj.toString());
			out.flush();
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
