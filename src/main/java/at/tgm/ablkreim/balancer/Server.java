package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class representing a connected server node, the load balancer can send
 * requests to.
 *
 * @author Klaus Ableitinger
 * @version 03.03.2016
 */
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

    private HandlerThread handlerThread;

    /**
     * Standard Server constructor.
     *
     * @param balancer the balancer who handles the server
     * @param connection the connection the server is connected to
     */
    public Server(LoadBalancer balancer, Connection connection) {

        this.balancer = balancer;
        this.connection = connection;
        this.sendLock = new ReentrantLock();
    }

    /**
     * Weighted distribution Server constructor.
     *
     * @param balancer the balancer who handles the server
     * @param connection the connection the server is connected to
     * @param weight the weight of this server
     */
    public Server(LoadBalancer balancer, Connection connection, int weight) {

        this(balancer, connection);
        this.weight = weight;
    }

    /**
     * Server Probes Server constructor.
     *
     * @param balancer the balancer who handles the server
     * @param connection the connection the server is connected to
     * @param loadConnection the connection used for updating the server load data
     */
    public Server(LoadBalancer balancer, Connection connection, Connection loadConnection) {

        this(balancer, connection);
        this.loadConnection = loadConnection;
    }

    /**
     * Returns the connection of this Server.
     *
     * @return the connection of this Server
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Get the weight for weighted distribution of this server
     * or 0 if not set.
     *
     * @return the weight of this server
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Returns the number of currently active requests to this server.
     *
     * @return the number of currently active requests to this server
     */
    public int getActiveConnections() {

        return responses.size();
    }

    /**
     * Starts a thread handling requests and responses.
     */
    public void startHandlingRequests() {

        handlerThread = new HandlerThread();
        handlerThread.start();
    }

    /**
     * Disconnects this server.
     */
    public void disconnect() {

        handlerThread.stopRunning();
        handlerThread.interrupt();
        connection.close();
    }

    /**
     * Sends the given request to the remote server.
     *
     * @param request the request to send
     */
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

    /**
     * Checks whether a response for the given request has been received.
     *
     * @param request the request the response should be for
     * @return whether there has been a response fo the given request
     */
    public boolean hasAcknowledge(PiRequest request) {

        LOG.debug("Checking for acknowledge from server: \"{}\"", connection.getName());
        if(!responses.containsKey(request))
            throw new IllegalArgumentException("Thread not waiting for an acknowledge of this command");

        return responses.get(request) != null;
    }

    /**
     * Blocking method to wait for a response of the given request.
     *
     * @param request the request the response should be for
     * @param timeout the maximum time to wait in ms
     * @return the received response
     * @throws InterruptedException when the thread gets interrupted while waiting
     * @throws TimeoutException if the waiting timed out
     */
    public PiResponse getAcknowledge(PiRequest request, int timeout) throws InterruptedException, TimeoutException {

        LOG.debug("Trying to get acknowledge from server: \"{}\"", connection.getName());

        long timestamp = System.currentTimeMillis();
        while(!hasAcknowledge(request)) {

            if(System.currentTimeMillis() - timestamp > timeout) throw new TimeoutException("Acknowledge getting timed out");
            Thread.sleep(100);
        }

        return responses.remove(request);
    }

    /**
     * Stops reacting to a response fo the given request
     *
     * @param request the request to stop waiting for
     */
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
    }
}
