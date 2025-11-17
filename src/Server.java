import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;

    private final ExecutorService pool = Executors.newCachedThreadPool();

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
                pool.submit(() -> handle(socket));
            }
        } catch (IOException ioe) {
            System.out.printf("Could not listen on port: %s", port);
            ioe.printStackTrace();
        }
    }

    private void handle(Socket clientSocket) {
        System.out.printf("Connected client: %s%n", clientSocket);

        try (
                clientSocket;
                Scanner reader = getReader(clientSocket);
                PrintWriter writer = getWriter(clientSocket)) {
            sendResponse("Hello " + clientSocket, writer);
            while (true) {
                String message = reader.nextLine().trim();
                if (isEmptyMessage(message) || isQuitMessage(message)) {
                    break;
                }
                sendResponse(message.toUpperCase(), writer);
            }
        } catch (NoSuchElementException nse) {
            System.out.println("Client dropped connection");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("Client disconnected: " + clientSocket);
    }

    private PrintWriter getWriter(Socket clientSocket) throws IOException {
        return new PrintWriter(clientSocket.getOutputStream());
    }

    private Scanner getReader(Socket clientSocket) throws IOException {
        return new Scanner(new InputStreamReader(clientSocket.getInputStream()));
    }

    private static boolean isQuitMessage(String msg) {
        return "stop".equalsIgnoreCase(msg);
    }

    private static boolean isEmptyMessage(String msg) {
        return msg == null || msg.isBlank();
    }

    private static void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }
}
