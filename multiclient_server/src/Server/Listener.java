package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Listener implements Runnable {
    private Socket socket;
    private String message = "";
    private ConcurrentLinkedQueue<String> executionQueue;

    Listener(Socket socket, ConcurrentLinkedQueue<String> executionQueue) {
        this.socket = socket;
        this.executionQueue = executionQueue;
    }

    @Override
    public void run() {
        try (BufferedReader serverDataInputStream = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {
            while (!this.message.strip().equals("stop")) {
                String line;
                while ((line = serverDataInputStream.readLine()) != null) {
                    this.message = line;
                    if (this.message.startsWith("CMD")) {
                        executionQueue.add(message);
                        System.out.println("LISTENER ::: Command Queued for Execution :" + message);
                    } else {
                        System.out.println("LISTENER ::: CLIENT says: " + this.message);
                    }
                }

            }
            System.out.println("LISTENER ::: Stopping to listen....");
            return;
        } catch (IOException e) {
            System.out.println("LISTENER ::: Stopping to listen....");
            return;
        }
    }
}

// package Server;
// import java.io.DataInputStream;
// import java.io.IOException;
// import java.net.Socket;
// import java.util.concurrent.ConcurrentLinkedQueue;

// public class Listener implements Runnable {
// private Socket socket;
// private String message = "";
// private ConcurrentLinkedQueue<String> executionQueue;

// Listener(Socket socket, ConcurrentLinkedQueue<String> executionQueue) {
// this.socket = socket;
// this.executionQueue = executionQueue;
// }

// @Override
// public void run() {
// try (DataInputStream serverDataInputStream = new
// DataInputStream(socket.getInputStream())) {
// while (!this.message.strip().equals("stop")) {
// while (serverDataInputStream.available() == 0)
// ;
// this.message = (String) serverDataInputStream.readUTF();
// if (this.message.startsWith("CMD")){
// executionQueue.add(message);
// System.out.println("LISTENER ::: Command Queued for Execution :" + message);
// }else{
// System.out.println("LISTENER ::: CLIENT says: " + this.message);
// }
// }
// System.out.println("LISTENER ::: Stopping to listen....");
// return;
// } catch (IOException e) {
// System.out.println("LISTENER ::: Stopping to listen....");
// return;
// }
// }
// }