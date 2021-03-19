import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebsocketClient {

    static WebSocketClient wsc;

    public static void connect() {
        URI uri;
        try {
            uri = new URI("ws://localhost:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        wsc = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("Connected to server!");
                sendMessage();

            }

            @Override
            public void onMessage(String message) {
                System.out.println(message);

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Left the room!");
            }

            @Override
            public void onError(Exception ex) {
                System.out.println(ex.getMessage());
            }
        };

        wsc.connect();

    }

    public static void sendMessage() {
        wsc.send("Hola aqui estoy");
    }

    public static void main(String Args[]) {
        connect();
    }
}
