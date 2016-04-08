package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

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
    private boolean run;

    public RequestHandler(Socket client, LoadBalancingAlgorithm loadBalancingAlgorithm) {
        this.run = true;
        this.connection = new Connection(client, "Client Connection #1");
        LOGGER.info("Accepted new Connection: {}", connection);
        this.loadBalancingAlgorithm = loadBalancingAlgorithm;
    }

    @Override
    public void run() {

        while(run) {
            PiRequest piRequest = this.connection.receive();

            if(piRequest == null) {
                LOGGER.error("Received invalid Object");
                break;
            }

            Server server = loadBalancingAlgorithm.send(piRequest);

            try {
                PiResponse piResponse = server.getAcknowledge(piRequest, 60000); // 1 Minute
                this.connection.send(piResponse);
            } catch(InterruptedException ex) {
                LOGGER.debug("Thread interrupted while waiting", ex);
            } catch(TimeoutException ex) {
                LOGGER.debug("Request timeout", ex);
            }
        }
    }
}
