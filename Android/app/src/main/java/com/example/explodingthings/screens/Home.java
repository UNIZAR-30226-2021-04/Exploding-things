package com.example.explodingthings.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.explodingthings.APIConnection.APIConnection;
import com.example.explodingthings.R;

public class Home extends AppCompatActivity {

    private Button buttonCreatePublic;
    private Button buttonJoinPublic;
    private ImageButton buttonLoggout;

    private APIConnection api;

    private String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        SharedPreferences sharedPref = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        id_user = sharedPref.getString("user","pepe");
        api = new APIConnection(this);

        buttonCreatePublic = findViewById(R.id.buttonCreatePublic);
        buttonJoinPublic = findViewById(R.id.buttonJoinPublic);
        buttonLoggout = findViewById(R.id.buttonLoggout);

        buttonLoggout.setOnClickListener((e) -> {
            SharedPreferences userName = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userName.edit();
            editor.putBoolean("logged", false);
            editor.apply();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });

        buttonCreatePublic.setOnClickListener((e) -> {
            api.createGameRequest(id_user, this);
        });

        buttonJoinPublic.setOnClickListener((e) -> {
            visualizeGameList();
        });
    }

    public void createLobby(int id_lobby){
        Intent intent = new Intent(this, Lobby.class);
        intent.putExtra("id_lobby",id_lobby);
        startActivity(intent);
    }

    private void visualizeGameList(){
        Intent intent = new Intent(this, GameList.class);
        startActivity(intent);
    }
}