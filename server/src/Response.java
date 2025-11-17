import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Response {
    private Response() {}

    public static void handleResponse(Socket clientSocket) {
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

    private static void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private static PrintWriter getWriter(Socket clientSocket) throws IOException {
        return new PrintWriter(clientSocket.getOutputStream());
    }

    private static Scanner getReader(Socket clientSocket) throws IOException {
        return new Scanner(new InputStreamReader(clientSocket.getInputStream()));
    }

    private static boolean isQuitMessage(String msg) {
        return "stop".equalsIgnoreCase(msg);
    }

    private static boolean isEmptyMessage(String msg) {
        return msg == null || msg.isBlank();
    }
}
