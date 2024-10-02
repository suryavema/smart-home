package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class ClientListener implements Runnable{
    private Socket socket;
    private String message = "";
    ClientListener(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream serverDataInputStream = new DataInputStream(socket.getInputStream())) {
            while (!this.message.strip().equals("stop")){
                while(serverDataInputStream.available() == 0);
                this.message = (String)serverDataInputStream.readUTF();
                System.out.println("SERVER: " + this.message);
            }
            System.out.println("Stopping to listen....");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Stopping to listen....");
            System.exit(-1);
        }
    }
}