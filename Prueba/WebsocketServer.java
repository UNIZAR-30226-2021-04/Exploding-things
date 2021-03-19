import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebsocketServer {

    public static void initialize() {

        WebSocketServer wss = new WebSocketServer(new InetSocketAddress(8080)) {

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println("Welcome to the server!");
                conn.send("Welcome to the server!");

            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                broadcast(conn + " has left the room!");
                System.out.println(conn + " has left the room!");
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                broadcast(message);
                System.out.println(conn + ": " + message);

            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                System.out.println(ex.getMessage());
            }

            @Override
            public void onStart() {
                System.out.println("Server started!");
                setConnectionLostTimeout(1000);
            }
        };

        wss.start();
    }

    public static void main(String Args[]) {
        initialize();
    }
}
