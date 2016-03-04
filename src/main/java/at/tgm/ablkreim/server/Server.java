package at.tgm.ablkreim.server;

import at.tgm.ablkreim.balancer.PiRequest;
import at.tgm.ablkreim.balancer.PiResponse;
import at.tgm.ablkreim.common.config.ServerConfig;
import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.URL;

/**
 * Class representing a Server, used to perform calculation tasks (calculate pi)
 *
 * @author Klaus Ableitinger
 * @version 26.02.2016
 */
public class Server {

    private static final Logger LOG = LogManager.getLogger(Server.class);

    /**
     * Starts a Server and connects to the LoadBalancer.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        new Server().start();
    }


    private ServerConfig config;
    private HandlerThread handlerThread;

    /**
     * Default Server constructor.
     */
    public Server() {

        try {

            URL url = getClass().getClassLoader().getResource("server_config.json");
            if(url == null)
                LOG.fatal("Cannot find config file");
                System.exit(1);

            config = new ServerConfig(new FileReader(url.getFile()));
        } catch(FileNotFoundException ex) {

            LOG.fatal("Cannot find config file", ex);
            System.exit(1);
        }
    }

    /**
     * Starts this server and connects to the LoadBalancer.
     */
    public void start() {

        handlerThread = new HandlerThread();
        handlerThread.start();
    }

    /**
     * Stops this server.
     */
    public void stop() {

        if(handlerThread != null) {

            handlerThread.stopRunning();
            handlerThread.interrupt();
        }
    }

    /**
     * Calculates Pi with the Leibniz row.
     *
     * @param begin begin index of the row
     * @param end end index of the row
     * @return the added value of the row
     */
    private BigDecimal calculateLeibniz(int begin, int end) {
        BigDecimal result = new BigDecimal(0.0);
        for(; begin <= end; ++begin) {

            result = result.add(new BigDecimal(Math.pow(-1, begin) / (2 * begin + 1)));
        }
        return result.multiply(new BigDecimal(4));
    }

    private class HandlerThread extends Thread {

        private boolean running = true;
        private Connection connection;

        @Override
        public void run() {

            LOG.info("Starting server handler");
            try {

                connection = new Connection(new Socket(config.getIP(), config.getPort()), "LoadBalancerConnection");

                while(running) {

                    PiRequest request = connection.receive();

                    if(request == null) {

                        LOG.error("Received invalid Object");
                        continue;
                    }

                    connection.send(new PiResponse(calculateLeibniz(request.begin, request.end), request.id));
                }
            } catch(Exception ex) {

                LOG.fatal("Exception in HandlerThread", ex);
            }
        }

        public void stopRunning() {

            running = false;
        }
    }
}