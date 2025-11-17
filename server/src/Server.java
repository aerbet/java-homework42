import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ClientManager clientManager = new ClientManager();

    private Server(int port) {
        this.port = port;
    }

    public static Server bindToServer(int port) {
        return new Server(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while(!server.isClosed()) {
                Socket socket = server.accept();
                pool.submit(new ClientHandler(socket, clientManager));
            }
        } catch (IOException ioe) {
            System.out.printf("Could not listen on port: %s", port);
            ioe.printStackTrace();
        }
    }
}
