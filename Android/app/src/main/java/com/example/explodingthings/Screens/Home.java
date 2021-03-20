package com.example.explodingthings.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;
import com.example.explodingthings.WebSocketConnection.WebSocketConnection;

import org.java_websocket.client.WebSocketClient;

public class Home extends AppCompatActivity {

    private Button buttonCreatePublic;
    private Button buttonJoinPublic;

    private APIConnection api;

    private int id_lobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        api = new APIConnection(this);

        buttonCreatePublic = findViewById(R.id.buttonCreatePublic);
        buttonJoinPublic = findViewById(R.id.buttonJoinPublic);

        buttonCreatePublic.setOnClickListener((e) -> {
            api.createGameRequest(5, this);
        });

        buttonJoinPublic.setOnClickListener((e) -> {
            Intent intent = new Intent(this, GameList.class);
            startActivity(intent);
        });
    }

    public void setLobby(int id_lobby){
        this.id_lobby = id_lobby;
        //Empezar actividad de Lobby con extra = id_lobby
    }
}