package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.concurrent.TimeoutException;

/**
 * @author mreilaender
 * @version 04.03.2016
 */
public class RequestHandler implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger(RequestHandler.class);
    private Connection connection;
    private LoadBalancingAlgorithm loadBalancingAlgorithm;

    public RequestHandler(Socket client, LoadBalancingAlgorithm loadBalancingAlgorithm) {
        this.connection = new Connection(client, client.getRemoteSocketAddress().toString());
        this.loadBalancingAlgorithm = loadBalancingAlgorithm;
    }

    @Override
    public void run() {
        PiRequest piRequest = this.connection.receive();
        Server server = loadBalancingAlgorithm.send(piRequest);
        try {
            PiResponse piResponse = server.getAcknowledge(piRequest, (int)(Math.random()*60)*1000);
            this.connection.send(piResponse);
        } catch (InterruptedException e) {
            LOGGER.debug("Thread interrupted while waiting", e);
        } catch (TimeoutException e) {
            LOGGER.debug("Request timeout", e);
        }

    }
}
