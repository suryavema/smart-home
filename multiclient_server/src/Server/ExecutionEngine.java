package Server;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
                System.out.println(piCommand);
                runtime.exec(piCommand);
                this.switches.set(Integer.parseInt(arr[1]), arr[2].equals("ON"));
                System.out.println("EXECUTION ENGINE ::: The swith "+ arr[1] + " is" + (this.switches.get(Integer.parseInt(arr[1])) ? "ON" : "OFF"));
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