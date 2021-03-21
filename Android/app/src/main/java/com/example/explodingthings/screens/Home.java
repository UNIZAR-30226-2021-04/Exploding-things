package com.example.explodingthings.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;

public class Home extends AppCompatActivity {

    private Button buttonCreatePublic;
    private Button buttonJoinPublic;

    private APIConnection api;

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
            visualizeGameList();
        });
    }

    public void joinLobby(int id_lobby){
        Intent intent = new Intent(this, GameList.class);
        intent.putExtra("id_lobby",id_lobby);
        startActivity(intent);
    }

    private void visualizeGameList(){
        Intent intent = new Intent(this, GameList.class);
        startActivity(intent);
    }
}