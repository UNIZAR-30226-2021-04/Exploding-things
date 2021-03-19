package com.example.explodingthings.WebSocketConnection;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class WebSocketConnection {

    private Context context;

    public WebSocketConnection(Context context) {
        this.context = context;
    }

    public void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("http://rocketruckus.westeurope.azurecontainer.io:8080/Lobby");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        WebSocketClient wsc = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("Websocket", "opened");
            }

            @Override
            public void onMessage(String message) {
                Log.d("Received: ", message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("Websocket", "closed " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i("Error", Objects.requireNonNull(ex.getMessage()));
            }
        };

        JSONObject jsonObject = new JSONObject();
        String msg = "";
        try {
            jsonObject.put("id_user","");
            jsonObject.put("id_lobby","");
            msg = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        wsc.connect();
        //wsc.send(msg);
    }

    private void messageDecode(String message){

    }
}
