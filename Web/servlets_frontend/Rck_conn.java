package servlets_frontend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Rck_conn {
	
	private final static String URL = "http://rocketruckus.westeurope.azurecontainer.io:8080/";
	
	public Rck_conn() {};

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
