package Server;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class Server {
    private static Vector<Boolean> switches = new Vector<>();
    private static ConcurrentLinkedQueue<String> executionQueue = new ConcurrentLinkedQueue<>();
    private static Vector<DataOutputStream> clientStreams = new Vector<>();
    private static AtomicBoolean executed = new AtomicBoolean(false);

    public static void main(String[] args) throws Exception {
        int portNumber = 5000;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            ExecutionEngine executionEngine = new ExecutionEngine(executionQueue, switches, executed);
            Thread executionThread = new Thread(executionEngine);
            Brodcaster brodcaster = new Brodcaster(switches, clientStreams, executed);
            Thread brodcastThread = new Thread(brodcaster);
            executionThread.start();
            brodcastThread.start();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            executor.setKeepAliveTime(5, TimeUnit.MINUTES);

            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    clientStreams.add(new DataOutputStream(client.getOutputStream()));
                    executor.submit(new Listener(client, executionQueue));
                } catch (IOException e) {
                    System.out.println("Accept Failed:");
                    System.exit(-1);
                }
            }
        }
    }
}
    // public static void main(String[] args) throws Exception {
    //     int portNumber = 5000;

    //     try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
    //         ExecutionEngine executionEngine = new ExecutionEngine(executionQueue, switches, executed);
    //         Thread executionThread = new Thread(executionEngine);
    //         Brodcaster brodcaster = new Brodcaster(switches, clientStreams, executed);
    //         Thread brodcastThread = new Thread(brodcaster);
    //         executionThread.start();
    //         brodcastThread.start();

    //         while (true) {
    //             try {
    //                 Socket client = serverSocket.accept();
    //                 clients.add(client);
    //                 clientStreams.add(new DataOutputStream(client.getOutputStream()));
    //                 Thread t = new Thread(new Listener(client, executionQueue));
    //                 t.start();
    //             } catch (IOException e) {
    //                 System.out.println("Accept Failed:");
    //                 System.exit(-1);
    //             }
    //         }
    //     }
    // }
// }
