package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

class Sender implements Runnable{
    private Socket socket;
    private Scanner sc = new Scanner(System.in);
    Sender(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        String message = "";
        try (DataOutputStream serverDataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            while (!message.equals("stop")){
                message = sc.nextLine();
                serverDataOutputStream.writeUTF(message);
                // System.out.println("me: "+ message);
            }
            serverDataOutputStream.close();
            System.out.println("Stopping to send....");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Stopping to send....");
            System.exit(-1);
        }
    }

}