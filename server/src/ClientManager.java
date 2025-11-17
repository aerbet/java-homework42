import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public ClientManager() {
    }

    public void registerClient(String name, ClientHandler clientHandler) {
        clients.put(name, clientHandler);
        System.out.println("Client " + name + " has been registered!");
    }

    public void unregisterClient(String name) {
        clients.remove(name);
        System.out.println("Client " + name + " has been unregistered!");
    }

    public void sendMessageToAllClients(String message, String senderName) {
        clients.forEach((clientName, clientHandler) -> {
            if (!clientName.equals(senderName)) {
                clientHandler.sendServerMessage(senderName + ": " + message);
            }
        });
    }
}
