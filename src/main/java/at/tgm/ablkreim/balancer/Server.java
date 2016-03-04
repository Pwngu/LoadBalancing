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

    private LoadBalancer balancer;
    private Connection connection;
    private Lock sendLock;

    // Server Probe
    private Connection loadConnection;
    private ServerLoad load;

    //Response Time
    private int currentResponseTime;

    //Weighted Distribution
    private int weight;

    public Server(LoadBalancer balancer, Connection connection) {

        this.balancer = balancer;
        this.connection = connection;
        this.sendLock = new ReentrantLock();
    }

    public Server(LoadBalancer balancer, Connection connection, int weight) {

        this(balancer, connection);
        this.weight = weight;
    }

    public Server(LoadBalancer balancer, Connection connection, Connection loadConnection) {

        this(balancer, connection);
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
        sendLock.lock();
        try {

            this.connection.send(request);
            responses.put(request, null);
        } finally {
            sendLock.unlock();
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

    private class HandlerThread extends Thread {

        private boolean running;

        @Override
        public void run() {

            thread:
            while(running) {

                Object obj = connection.receive();
                if(obj == null) {

                    break;
                } else if(obj instanceof String) {

                    LOG.info("Message from server \"{}\": {}", connection.getName(), obj);
                } else if(obj instanceof PiResponse) {

                    PiResponse response = (PiResponse) obj;

                    for(PiRequest request : responses.keySet())
                        if(response.id == request.id) {

                            responses.put(request, response);
                            LOG.debug("Received response for request:\t");
                            continue thread;
                        }

                    LOG.debug("Received acknowledge for command not waiting for");
                }
            }

            LOG.warn("Disconnecting Server {}", connection.getName());
            balancer.disconnect(Server.this);
        }

        public void stopRunning() {

            running = false;
        }

        public int getWeight() {
            return weight;
        }
    }
}
