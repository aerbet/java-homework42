import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ClientManager clientManager;
    private String clientName;
    private Writer writer;

    public ClientHandler(Socket socket, ClientManager clientManager) {
        this.socket = socket;
        this.clientManager = clientManager;
        this.clientName = "User-" + socket.getPort();
    }

    public void run() {
        try (
            socket;
            Scanner reader = getReader();
            PrintWriter writer = getWriter()) {
            this.writer = writer;

            clientManager.registerClient(clientName.trim(), this);
            sendResponse("Hello " + clientName + ". You are successfully registered in our chat.");
            while (true) {
                if (reader.hasNextLine()) {
                    String message = reader.nextLine().trim();
                    if (isEmptyMessage(message) || isQuitMessage(message)) {
                        break;
                    }
                    clientManager.sendMessageToAllClients(message, clientName);
                } else {
                    break;
                }
            }
        } catch (NoSuchElementException nse) {
            System.out.println("Client dropped connection");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        clientManager.unregisterClient(clientName);
        clientManager.sendMessageToAllClients(clientName + " exit from our chat", "Server");
    }

    public void sendServerMessage(String message) {
        try {
            writer.write(message);
            writer.write(System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to " + clientName + ": " + e.getMessage());
        }
    }

    private void sendResponse(String response) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private PrintWriter getWriter() throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

    private Scanner getReader() throws IOException {
        return new Scanner(new InputStreamReader(socket.getInputStream()));
    }

    private boolean isQuitMessage(String msg) {
        return "stop".equalsIgnoreCase(msg);
    }

    private boolean isEmptyMessage(String msg) {
        return msg == null || msg.isBlank();
    }
}
