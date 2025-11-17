import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private Scanner reader;
    private Writer writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.printf("Connected client: %s%n", socket);
        try (
            socket;
            Scanner reader = getReader();
            PrintWriter writer = getWriter()) {
            this.reader = reader;
            this.writer = writer;

            sendResponse("Hello " + socket);
            while (true) {
                if (reader.hasNextLine()) {
                    String message = reader.nextLine().trim();
                    if (isEmptyMessage(message) || isQuitMessage(message)) {
                        break;
                    }
                    sendResponse(message.toUpperCase());
                } else {
                    break;
                }
            }
        } catch (NoSuchElementException nse) {
            System.out.println("Client dropped connection");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("Client disconnected: " + socket);

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
