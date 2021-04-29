package servlets_frontend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Rck_conn {
	
	private final static String URL = "http://localhost:8080/RocketRuckus/";
	//private final static String URL = "http://localhost:8080/RocketRuckus/";
	private final static String WS = "ws://localhost:8080/RocketRuckus/";
	//private final static String WS = "ws://localhost:8080/RocketRuckus/";
	
	public final static String LOBBYWS = WS+"LobbyWS";
	public final static String PARTIDAWS = WS+"PartidaWS";
	public final static String INVITARWS = WS+"InvitarWS";
	
	public Rck_conn() {};

	/**
	 * Conecta con otro servlet, añadiendo a la URL los parámetros proporcionados.
	 * Recibe un objeto de tipo JSONObject con la respuesta.
	 * 
	 * @param parameters :Parámetros extra de la URL
	 * 
	 * @return obj (JSONObject con la respuesta del servlet)
	 * 
	 * @throws IOException
	 */
	public JSONObject connect(String parameters) throws IOException {
		
		URL url = new URL(URL+parameters);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String jotason = IOUtils.toString(in, encoding);
		JSONObject obj = new JSONObject(jotason);
		
		return obj;
		
	}
	
	/**
	 * Conecta con otro servlet, añadiendo a la URL los parámetros proporcionados.
	 * Recibe un objeto de tipo JSONArray con la respuesta.
	 * 
	 * @param parameters :Parámetros extra de la URL
	 * 
	 * @return obj (JSONArray con la respuesta del servlet)
	 * 
	 * @throws IOException
	 */
	public JSONArray connectArray(String parameters) throws IOException {
		
		URL url = new URL(URL+parameters);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String jotason = IOUtils.toString(in, encoding);
		JSONArray obj = new JSONArray(jotason);
		
		return obj;
		
	}

}
