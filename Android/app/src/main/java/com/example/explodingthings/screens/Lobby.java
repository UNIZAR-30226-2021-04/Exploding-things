package com.example.explodingthings.screens;

import android.os.Bundle;
import android.util.Log;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Lobby extends AppCompatActivity {

    private ImageButton goBackButton;
    private Button startButton;
    private TextView firstPlayer;
    private TextView secondPlayer;
    private TextView thirdPlayer;
    private TextView fourthPlayer;

    private APIConnection api;
    private WebSocket ws;
    private OkHttpClient client;

    private int numUsers;
    private int id_user;
    private int id_lobby;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_lobby);
        //¿Recibir de la actividad padre la conexion WebSocket establecida al crear la sala?
        api = new APIConnection(this);

        url = "ws://rocketruckus.westeurope.azurecontainer.io:8080/LobbyWS";
        goBackButton = findViewById(R.id.buttonBack);
        startButton =  findViewById(R.id.buttonStartGame);
        firstPlayer = findViewById(R.id.textPlayer1);
        secondPlayer = findViewById(R.id.textPlayer2);
        thirdPlayer  = findViewById(R.id.textPlayer3);
        fourthPlayer = findViewById(R.id.textPlayer4);

        startButton.setOnClickListener ((e) -> {
            requestStart();
            //Comprobar numero de players en la sala y si es >= 2 transicion a game screen
        });
        goBackButton.setOnClickListener((e) -> {
            sendMessage(2,4,"salir", ws);
            api.exitLobbyRequest(1, id_lobby,this);
        });

        client = new OkHttpClient();
        startClientEndpoint();

    }
    private void requestStart(){


    }

    private void fillData(){
        //Aqui la idea sería ponernos a nosotros como jugador 1(De momento nuestro username,+ tarde nuestro icon) y distribuir al resto en los huecos libres
        //PROBLEMA:Esto se tiene que hacer también cada vez que recibamos la info de que alguien se ha unido o ha salido de la sala
        //No es suficiente con hacerlo al entrar.
    }

    /**
     * Mensaje que se envía en formato JSON al endpoint del servidor
     * @param id_user identificador del usuario
     * @param id_lobby identificador de la sala a la que está conectado el usuario
     * @param msg_type "crear" para empezar la partida
     *                 "salir" para salir de la sala
     * @param ws WebSocket creado para la comunciación
     */
    private void sendMessage(int id_user, int id_lobby, String msg_type, WebSocket ws){
        JSONObject jsonObject = new JSONObject();
        String msg = null;
        try {
            jsonObject.put("msg_type",msg_type);
            jsonObject.put("id_user", id_user);
            jsonObject.put("id_lobby",id_lobby);
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
    private void startClientEndpoint() {
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
                // Cada vez que llegue algo del servidor
                // actualizamos la informacion de la pagina
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                Log.d("Websocket", "connected to server");
                ws = webSocket;
            }
        });
        client.dispatcher().executorService().shutdown();
    }

}