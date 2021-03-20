package com.example.explodingthings.Screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;
import com.example.explodingthings.WebSocketConnection.WebSocketConnection;

public class Lobby extends AppCompatActivity {

    private ImageButton goBackButton;
    private Button startButton;
    private TextView firstPlayer;
    private TextView secondPlayer;
    private TextView thirdPlayer;
    private TextView fourthPlayer;

    private WebSocketConnection webSocketConnection;
    private APIConnection api;

    private int numUsers;
    private int id_user;
    private int id_lobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_lobby);
        //¿Recibir de la actividad padre la conexion WebSocket establecida al crear la sala?
        webSocketConnection = new WebSocketConnection(this);//Lo dejo asi para que no de error,hay que cambiarlo por la heredada

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
            api.exitLobbyRequest(1, id_lobby,this);
            //Cerramos sesion en la sala y vamos para atrás
        });
    }
    private void requestStart(){
        //Creo que de momento backend no ha definido que mensaje mandar en este caso

    }
    private void requestExit(){
        //id_user + id_lobby + f=d
        //Hay que mandar mensaje , DUDA: A través de la API o a través de WebSocket

    }
    private void fillData(){
        //Aqui la idea sería ponernos a nosotros como jugador 1(De momento nuestro username,+ tarde nuestro icon) y distribuir al resto en los huecos libres
        //PROBLEMA:Esto se tiene que hacer también cada vez que recibamos la info de que alguien se ha unido o ha salido de la sala
        //No es suficiente con hacerlo al entrar.
    }

}
//De alguna forma estamos escuchando de la conexion establecida con el webSocket y cada vez que nos llegue info de cambios en el lobby
//estos deberian reflejarse en la pantalla.
//El numero de usuarios en el lobby tambien lo obtenemos de las modificaciones
//Mi idea: Ponemos por defecto Nombres e imagenes que indiquen que ese usuario no esta conectado , y cuando tengamos una nueva conexión
//los sustituimos por los datos del usuario nuevo que haya entrado.