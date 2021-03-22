package com.example.explodingthings.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Lobby extends AppCompatActivity {

    private ImageButton goBackButton;
    private Button startButton;
    private ArrayList<TextView> userTextList;

    private APIConnection api;
    private WebSocket ws;

    private int numUsers = 0;
    private final int totalUsers = 4;
    private String id_user;
    private int id_lobby;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_lobby);
        SharedPreferences sharedPref = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        id_user = sharedPref.getString("user","pepe");
        api = new APIConnection(this);
        url = "ws://rocketruckus.westeurope.azurecontainer.io:8080/LobbyWS";

        goBackButton = findViewById(R.id.buttonBack);
        startButton =  findViewById(R.id.buttonStartGame);

        initUsers();
        addUser(id_user);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            id_lobby = 0;
        } else {
            id_lobby = extras.getInt("id_lobby");
        }

        startButton.setOnClickListener ((e) -> {
            requestStart();
            //Comprobar numero de players en la sala y si es >= 2 transicion a game screen
        });
        goBackButton.setOnClickListener((e) -> {
            onBackPressed();
        });

        OkHttpClient client = new OkHttpClient();
        startClientEndpoint(client);

    }
    private void requestStart(){
        //Iniciar partida
    }

    private void fillData(){

    }

    /**
     * Mensaje que se envía en formato JSON al endpoint del servidor
     * @param id_user identificador del usuario
     * @param id_lobby identificador de la sala a la que está conectado el usuario
     * @param msg_type "crear" para empezar la partida
     *                 "salir" para salir de la sala
     * @param ws WebSocket creado para la comunciación
     */
    private void sendMessage(String id_user, int id_lobby, String msg_type, WebSocket ws){
        JSONObject jsonObject = new JSONObject();
        String msg = "";
        try {
            jsonObject.put("msg_type",msg_type);
            jsonObject.put("id_user", id_user);
            jsonObject.put("id_lobby",id_lobby+"");
            msg = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ws.send(msg);
    }

    /**
     * Abrimos el endpoint cliente del websocket, el cual se comunica con el endpoint
     * del servidor
     */
    private void startClientEndpoint(OkHttpClient client) {
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                Log.i("Websocket", "Error: " + reason);
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                Log.i("Websocket", "Error: " + reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Log.i("Websocket", "Error: " + t.getMessage());
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                Log.d("Received: ", text);
                try {
                    JSONObject object = new JSONObject(text);
                    String name = object.getString("id_user");
                    String msg_type = object.getString("msg_type");
                    switch (msg_type) {
                        case "connected":
                            addUser(name);
                            break;
                        case "disc":
                            deleteUser(name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                Log.d("Websocket", "connected to server");
                ws = webSocket;
                sendMessage(id_user,id_lobby,"crear",ws);
            }
        });
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        sendMessage(id_user, id_lobby, "salir", ws);
        api.exitLobbyRequest(id_user, id_lobby);
    }

    public void addUser(String id_user){
        runOnUiThread(() -> {
            userTextList.get(numUsers).setText(id_user);
            numUsers++;
        });
    }

    public void deleteUser(String id_user){
        runOnUiThread(() -> {
            for(int i=0; i<totalUsers; i++){
                if (userTextList.get(i).getText().toString().equals(id_user)){
                    userTextList.get(i).setText("Esperando...");
                    break;
                }
            }
        });
        numUsers--;
    }

    private void initUsers(){
        userTextList = new ArrayList<>();
        userTextList.add(findViewById(R.id.textPlayer1));
        userTextList.add(findViewById(R.id.textPlayer2));
        userTextList.add(findViewById(R.id.textPlayer3));
        userTextList.add(findViewById(R.id.textPlayer4));
    }

}