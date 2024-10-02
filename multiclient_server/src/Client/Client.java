package Client;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        Socket clientSocket;
        try {
            // clientSocket = new Socket("192.168.212.79", 5000);
            clientSocket = new Socket("localhost", 5000);
            ClientListener listener = new ClientListener(clientSocket);
            Thread listenThread = new Thread(listener);
            Sender sender = new Sender(clientSocket);
            Thread sendThread = new Thread(sender);
            listenThread.start();
            sendThread.start();
            listenThread.join();
            sendThread.join();
            clientSocket.close();
        } catch (UnknownHostException e) {
            System.out.println("Server Not Available at the given port. EXITING ...");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Some Error Occured");
            e.printStackTrace();
        }
    }
}
