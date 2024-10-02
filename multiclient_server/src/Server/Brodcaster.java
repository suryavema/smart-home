package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
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