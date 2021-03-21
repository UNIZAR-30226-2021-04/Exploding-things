package com.example.explodingthings.APIConnection;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.explodingthings.screens.GameList;
import com.example.explodingthings.screens.Home;
import com.example.explodingthings.screens.Lobby;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class APIConnection {

    private Context context;
    private final String Url = "http://rocketruckus.westeurope.azurecontainer.io:8080/";

    public APIConnection(Context context){
        this.context = context;
    }

    public void loginRequest(String user, String pass, Class<?> cls){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                this.Url + "LoginUser?user=" + user + "&pass=" + pass,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        boolean logged = object.getBoolean("result");
                        Log.d("Login", logged + "");
                        if (logged) {
                            Intent intent = new Intent(this.context, cls);
                            this.context.startActivity(intent);
                        } else {
                            Toast.makeText(this.context, "Usuario o contraseña" +
                                    "mal introducidos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void gameListRequest(GameList gl){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                this.Url + "Lobby",
                response -> {
                    try {
                        JSONArray object = new JSONArray(response);
                        ArrayList<Object> gameList = toStringArray(object);
                        gl.fillData((String[])gameList.get(0), (int[])gameList.get(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void createGameRequest(int id_user, Home home){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                this.Url + "Lobby?id_user=" + id_user,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        int id_lobby = object.getInt("id_lobby");
                        Log.d("Game created", id_lobby + "");
                        home.createLobby(id_lobby);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void joinLobbyRequest(int id_user, int id_lobby, GameList gl){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                this.Url + "Lobby?id_user=" + id_user + "&id_lobby=" + id_lobby + "&f=u",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        boolean joined = object.getBoolean("result");
                        Log.d("Lobby join", joined + "");
                        gl.joinLobby();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void exitLobbyRequest(int id_user, int id_lobby, Lobby lobby){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                this.Url + "Lobby?id_user=" + id_user + "&id_lobby=" + id_lobby + "&f=u",null, null);
        queue.add(stringRequest);
    }

    /**
     * Convierte el JSONArray recibido a lista de arrays
     * @param array JSONArray que se recibe {"nuser":val,"id_lobby":val}
     * @return lista de arrays --> Array1 = id_users; Array2 = id_lobbys
     */
    private static ArrayList<Object> toStringArray(JSONArray array) {
        if(array == null)
            return null;
        String[] lobbyList = new String[array.length()];
        int[] userList = new int[array.length()];
        try {
            for (int i = 0; i < array.length(); i++) {
                userList[i] = Integer.parseInt((array.getJSONObject(i).getString("nuser")));
                lobbyList[i] = (array.getJSONObject(i).getString("id_lobby"));
                Log.d("GameList", userList[i] + " " + lobbyList[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<Object> ret = new ArrayList<>();
        ret.add(lobbyList);
        ret.add(userList);
        return ret;
    }

    public void registerRequest(){

    }

}
