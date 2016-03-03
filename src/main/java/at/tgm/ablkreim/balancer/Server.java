package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private static final Logger LOG = LogManager.getLogger(Server.class);

    private HashMap<PiRequest, PiResponse> responses;

    private Connection connection;
    private Lock lock;

    // Server Probe
    private Connection loadConnection;
    private ServerLoad load;

    //Response Time
    private int currentResponseTime;

    //Weighted Distribution
    private int weight;

    public Server(Connection connection) {

        this.connection = connection;
        this.lock = new ReentrantLock();
    }

    public Server(Connection connection, int weigth) {

        this(connection);
        this.weight = weigth;
    }

    public Server(Connection connection, Connection loadConnection) {

        this(connection);
        this.loadConnection = loadConnection;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getActiveConnections() {

        return responses.size();
    }


    public void sendRequest(PiRequest request) {

        LOG.debug("Sending request to server: \"{}\"", connection.getName());
        lock.lock();
        try {

            this.connection.send(request);
            responses.put(request, null);
        } finally {
            lock.unlock();
        }
    }

    public boolean hasAcknowledge(PiRequest request) {

        LOG.debug("Checking for acknowledge from server: \"{}\"", connection.getName());
        if(!responses.containsKey(request))
            throw new IllegalArgumentException("Thread not waiting for an acknowledge of this command");

        return responses.get(request) != null;
    }

    public PiResponse getAcknowledge(PiRequest request, int timeout) throws InterruptedException, TimeoutException {

        LOG.debug("Trying to get acknowledge from server: \"{}\"", connection.getName());

        long timestamp = System.currentTimeMillis();
        while(!hasAcknowledge(request)) {

            if(System.currentTimeMillis() - timestamp > timeout) throw new TimeoutException("Acknowledge getting timed out");
            Thread.sleep(100);
        }

        return responses.remove(request);
    }

    public void abortWaitForAcknowledge(PiRequest request) {

        LOG.debug("No longer waiting for a acknowledge from server: \"{}\"", connection.getName());

        responses.remove(request);
    }
}
