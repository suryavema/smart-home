import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class Brodcaster implements Runnable {
    private Vector<Boolean> switches;
    private Vector<DataOutputStream> clientStreams;
    AtomicBoolean executed;

    Brodcaster(Vector<Boolean> switches, Vector<DataOutputStream> clientStreams, AtomicBoolean executed) {
        this.switches = switches;
        this.executed = executed;
        this.clientStreams = clientStreams;
    }

    @Override
    public void run() {
        while (true) {
            while (!executed.get() || clientStreams.isEmpty())
                ;
            for (DataOutputStream serverDataOutputStream : clientStreams) {

                try {
                    serverDataOutputStream.writeUTF(switches.toString());
                    executed.set(false);
                    ;
                } catch (IOException e) {

                }
            }
        }
    }

}
class ExecutionEngine implements Runnable {
    private AtomicBoolean executed;

    private ConcurrentLinkedQueue<String> executionQueue;
    private Vector<Boolean> switches;
    private Runtime runtime;
    private HashMap<Integer, Integer> indexToPinMap = new HashMap<>();

    ExecutionEngine(ConcurrentLinkedQueue<String> executionQueue, Vector<Boolean> switches, AtomicBoolean executed) {
        this.executed = executed;
        this.executionQueue = executionQueue;
        this.switches = switches;
        runtime = Runtime.getRuntime();
        indexToPinMap.put(0, 17);
        indexToPinMap.put(1, 27);
        indexToPinMap.put(2, 22);
        indexToPinMap.put(3, 23);
        indexToPinMap.put(4, 24);
    }

    private void executeCommand(String command) {
        if (this.switches.size() == 0) {
            this.switches.add(false);
            this.switches.add(false);
            this.switches.add(false);
            this.switches.add(false);
            this.switches.add(false);
        }
        String[] arr = command.split(" ");
        try {
            if (arr.length == 3){
                String piCommand = "raspi-gpio set " + indexToPinMap.get(Integer.parseInt(arr[1])) + (arr[2].equals("ON") ? " op dh" : " op dl");
                System.out.println("EXECUTION ENGINE ::: EXECUTING :: "+ piCommand);
                runtime.exec(piCommand);
                this.switches.set(Integer.parseInt(arr[1]), arr[2].equals("ON"));
                System.out.println("EXECUTION ENGINE ::: The swith "+ arr[1] + " is " + (this.switches.get(Integer.parseInt(arr[1])) ? "ON" : "OFF"));
                this.executed.set(true);
            }else{
                this.executed.set(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            while (executionQueue.isEmpty())
                ;
            executeCommand(executionQueue.remove());
        }
    }

}
class Listener implements Runnable {
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
public class FinalServer {
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
