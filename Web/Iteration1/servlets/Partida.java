package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.ManosDAO;
import dao.ManosDAO.Mazo;
import org.json.*;

/**
 * Servlet para el login de los usuarios
 */
@WebServlet("/Partida")
public class Partida extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Partida() {
        super();
        // TODO Auto-generated constructor stub
    }

	/** 
	 * Parametros: user(string, nombre del usuario), host(bool, true si es el host lobby false en caso contrario), id_lobby(integer, id lobby)
	 * Devuelve JSONObject con un JSONArray mano: con el nombre de cada carta del jugador (5 cartas, strings) ["Barajar","Escape","Escape",..],
	 * numuser: (integer, numero de usuarios lobby) y jsonarray mazo: de la misma forma que mano (solo se devuelve si host=true).
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ManosDAO facade = new ManosDAO();	//Variable para el uso de las funciones de la clase ManosDAO
		ArrayList<Mazo> cartas = facade.devolverMano(request.getParameter("user"));	//Array de cartas del usuario
		boolean host = Boolean.parseBoolean(request.getParameter("host"));			//Booleano que indica si el que ha llamado al servlet es el host
		Integer id_lobby = Integer.parseInt(request.getParameter("id_lobby"));	//Id del lobby que va a empezar a jugar
		JSONObject obj = new JSONObject();
		JSONArray mano = new JSONArray();	//Array que contendra el nombre de las cartas del usuario 
		
		
		if (host) { 
			/*
			 * En caso de que el host nos haya llamado hay que actualizar la base de datos para indicar que
			 * la partida ya ha comenzado
			 * y devuelve el mazo
			 */

			facade.comenzarPartida(id_lobby);
			ArrayList<String> mazo = facade.devolverMazo(id_lobby);
			JSONArray obj2 = new JSONArray();
			for (int i = 0; i < mazo.size(); i++) {
				obj2.put(mazo.get(i));
			}
			obj.put("mazo", obj2);
			
		}
	
		/*
		 * Bucle para la conversion de la variable cartas en JSONArray
		 * En el array se meten los nombres de las cartas que componen la mano del jugador
		 */
		for (int i = 0; i < cartas.size(); i++) {
			
			for(int j=0;j<cartas.get(i).getNum();j++) {
				
				mano.put(cartas.get(i).getId_carta());
				
			}
			
		}
		obj.put("mano",mano);
		obj.put("numuser", facade.numUsuarios(id_lobby));
		
		/*
		 * Creacion y envio del JSON por el descriptor
		 */
		PrintWriter out = response.getWriter();
		out.print(obj.toString());
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
