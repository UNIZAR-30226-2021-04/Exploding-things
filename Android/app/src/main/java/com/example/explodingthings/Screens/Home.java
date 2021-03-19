package com.example.explodingthings.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.explodingthings.R;
import com.example.explodingthings.WebSocketConnection.WebSocketConnection;

import org.java_websocket.client.WebSocketClient;

public class Home extends AppCompatActivity {

    private Button buttonCreatePublic;
    private WebSocketConnection webSocketConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        buttonCreatePublic = findViewById(R.id.buttonCreatePublic);

        webSocketConnection = new WebSocketConnection(this);

        buttonCreatePublic.setOnClickListener((e) -> {
            webSocketConnection.connectWebSocket();
        });
    }
}