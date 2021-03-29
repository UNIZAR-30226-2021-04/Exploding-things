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
import dao.ManosDAO;

import org.json.*;

/**
 * Servlet para el uso de las funciones relacionadas con los lobbys
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
	 * Ver lobbys activos: Sin parametros en la URL, devuelve JSONArray con id_lobby:(integer, id lobby activo) y nuser:(integer, num usuarios lobby).
	 * Crear lobby: Parametros URL id_user=string (Nombre del usuario), devuelve JSON id_lobby:(integer, id lobby creado)
	 * Unirse a lobby: Parametros URL id_user=string (Nombre del usuario), id_lobby=integer(id lobby al que unirse), f=u
	 * 		   devuelve JSON con result:(bool, true si se ha podido unir, false en caso contrario)
	 * Salir de la lobby: Parametros URL id_user=string (Nombre del usuario), id_lobby=integer(id lobby del que sale), f=d
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LobbyDAO facade = new LobbyDAO();		//Variable para a utilizacion de las funciones de la clase LobbyDAO
		PrintWriter out = response.getWriter();	//Descriptor por el que se va a enviar la informacion pedida en formaton JSON
		ManosDAO cartas =  new ManosDAO();
		
		if (request.getParameter("id_user") == null) {
			
			/*
			 * En caso de que no se haya pasado como parametro el id del usuario eso implica que se
			 * tiene que devolver una lista con los ids de los lobbys en loso que un usuario se podria meter
			 */
			JSONArray obj = new JSONArray();	//Array que contendra el id de los lobbys disponibles junto al numero de usuarios dentro de estos
			
			ArrayList<Integer[]> lobbys = facade.SeleccionarLobbys();	//Matriz con el id de cada uno de los lobbys junto al numero de usuarios dentro de este
			
			/*
			 * Bucle para la conversion de la variable lobbys en JSONArray
			 */
			for (int i = 0; i < lobbys.size(); i++) {

				/*
				 * Creacion del JSONObject que se va a introducir dentro del JSONArray
				 */
				JSONObject aux = new JSONObject();
				aux.put("nuser", lobbys.get(i)[1]);
				aux.put("id_lobby", lobbys.get(i)[0]);
				
				obj.put(aux);
			
			}
			
			/*
			 * Envio por el descriptor el JSON creado
			 */
			out.print(obj.toString());
			out.flush();
			
		}
		else {
			
			JSONObject obj = new JSONObject();
			
			String id_user= request.getParameter("id_user");	//Variable que almacena el id del usuario que realiza la accion
			
			/*
			 * Si el id del usuario se ha pasado por parametro eso indica que se quiere crear una sala o realizar
			 * una accion dentro de una como salir o unirse
			 */
			if (request.getParameter("id_lobby")==null) {
				
				/*
				 * En caso de que no se haya pasado por parametro el id de la sala eso implica que se 
				 * quiere crear una por lo que se llama al metodo de la clase que crea salas y se devuelve
				 * el resultado obtenido en el JSON
				 * Ademas, se crea el mazo y la mano del jugador
				 */
				Integer id_lobby = facade.CrearLobby(id_user);
				obj.put("id_lobby",id_lobby);
				cartas.crearMazo(id_lobby);
				cartas.crearMano(id_lobby,id_user);
				
				
			}
			else {
				
				Integer id_lobby = Integer.parseInt(request.getParameter("id_lobby"));	//Variable que guarda el id del lobby sobre el que se quiere realizar la accion
				String f = request.getParameter("f");									//Accion a realizar
				
				switch (f) { 
					case "d":
						
						/*
						 * Se realiza la desconexión del lobby
						 */
						cartas.returnMazo(id_user, id_lobby);
						facade.DesconectarLobby(id_user, id_lobby);
						
						break;
					
					case "u":
						
						/*
						 * Se agnade al usuario dentro del lobby y se crea su mano de cartas
						 */
						boolean unido = facade.UnirLobby(id_user, id_lobby);
						obj.put("result",unido);
						if (unido) {cartas.crearMano(id_lobby,id_user);}
						
						
						break;
					
				}
				
			}
			
			/*
			 * Envio por el descriptor el JSON creado
			 */
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
