package com.example.explodingthings.Screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import okhttp3.WebSocket;

public class Lobby extends AppCompatActivity {

    private ImageButton goBackButton;
    private Button startButton;
    private TextView firstPlayer;
    private TextView secondPlayer;
    private TextView thirdPlayer;
    private TextView fourthPlayer;

    private APIConnection api;
    private WebSocketClient wsc;

    private int numUsers;
    private int id_user;
    private int id_lobby;

    private URI uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_lobby);
        //¿Recibir de la actividad padre la conexion WebSocket establecida al crear la sala?
        api = new APIConnection(this);

        goBackButton = findViewById(R.id.buttonBack);
        startButton =  findViewById(R.id.buttonStartGame);
        firstPlayer = findViewById(R.id.textPlayer1);
        secondPlayer = findViewById(R.id.textPlayer2);
        thirdPlayer  = findViewById(R.id.textPlayer3);
        fourthPlayer = findViewById(R.id.textPlayer4);
        try {
            uri = new URI("ws://rocketruckus.westeurope.azurecontainer.io:8080/LobbyWS");
        } catch (
                URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        startButton.setOnClickListener ((e) -> {
            requestStart();
            //Comprobar numero de players en la sala y si es >= 2 transicion a game screen
        });
        goBackButton.setOnClickListener((e) -> {
            api.exitLobbyRequest(1, id_lobby,this);
        });
/*
        wsc = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("Websocket", "connected to server");
                sendMessage(2,5,"crear");
            }

            @Override
            public void onMessage(String message) {
                Log.d("Received: ", message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("Websocket", "Closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i("Websocket", "Error: " + Objects.requireNonNull(ex.getMessage()));
            }
        };

        wsc.connect();
 */


    }
    private void requestStart(){
        //Creo que de momento backend no ha definido que mensaje mandar en este caso

    }

    private void fillData(){
        //Aqui la idea sería ponernos a nosotros como jugador 1(De momento nuestro username,+ tarde nuestro icon) y distribuir al resto en los huecos libres
        //PROBLEMA:Esto se tiene que hacer también cada vez que recibamos la info de que alguien se ha unido o ha salido de la sala
        //No es suficiente con hacerlo al entrar.
    }

    private void sendMessage(int id_user, int id_lobby, String msg_type){
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
        wsc.send(msg);
    }

}