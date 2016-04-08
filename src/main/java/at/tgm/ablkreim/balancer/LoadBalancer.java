package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.config.LoadBalancerConfig;
import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.server.InputStreamLogEventBridge;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO closing server socket or set running false in RequestHandler and AcceptHandler
 *
 * @author Manuel Reiländer
 * @version 03.03.2016
 */
public class LoadBalancer {
    private static final Logger LOGGER = LogManager.getLogger(LoadBalancer.class);


    private LoadBalancerConfig loadBalancerConfig;
    private LoadBalancingAlgorithm loadBalancingAlgorithm;
    private ServerSocket servers, clients;
    private List<Server> connectedServers;

    private boolean run;

    /**
     * Default LoadBalancer constructor.
     */
    public LoadBalancer() {

        run = true;

        LOGGER.info("Starting Loadbalancer");
        this.connectedServers = new ArrayList<>();

        InputStream stream = getClass().getResourceAsStream("/loadBalancer_config.json");
        if (stream == null) {
            LOGGER.fatal("Cannot find config file");
            System.exit(1);
        }
        loadBalancerConfig = new LoadBalancerConfig(new InputStreamReader(stream));
        LOGGER.debug(loadBalancerConfig);

        switch (loadBalancerConfig.getLoadBalancerAlgorithm()) {
            case LoadBalancingAlgorithm.WEIGHTED_DISTRIBUTION:
                LOGGER.info("Using Least Connection Balancing method");
                this.loadBalancingAlgorithm = new WeightedDistribution();
                break;
            case LoadBalancingAlgorithm.LEAST_CONNECTION:
                LOGGER.info("Using Least Connection Balancing method");
                this.loadBalancingAlgorithm = new LeastConnection();
                break;
            case LoadBalancingAlgorithm.ROUND_ROBIN:
                LOGGER.info("Using Least Connection Balancing method");
                this.loadBalancingAlgorithm = new RoundRobin();
                break;
            case LoadBalancingAlgorithm.SERVER_PROBES:
                throw new UnsupportedOperationException();
            default:
                LOGGER.fatal("Unknown Algorithm");
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Starts the LoadBalancer listening for server and client connections.
     */
    public void start() {
        try {
            LOGGER.debug("Reading config");
            InetAddress serverHost = InetAddress.getByName(loadBalancerConfig.getServerIP());
            InetAddress clientHost = InetAddress.getByName(loadBalancerConfig.getIP());

            LOGGER.debug("Creating ServerSockets");
            this.servers = new ServerSocket(loadBalancerConfig.getServerPort(), 50, serverHost);
            this.clients = new ServerSocket(loadBalancerConfig.getPort(), 50, clientHost);

            new Thread(new ClientAcceptHandler()).start();

            LOGGER.info("Waiting for new Server connections");
            while(run) {

                try {
                    Socket client = servers.accept();
                    LOGGER.info("New Server Connection");
                    Connection connection = new Connection(client, "Server Connection #" + connectedServers.size());
                    int weight = 0;

                    if(loadBalancerConfig.getLoadBalancerAlgorithm() == LoadBalancingAlgorithm.WEIGHTED_DISTRIBUTION) {
                        weight = connection.receive();
                        LOGGER.debug("Initialize Server with weight " + weight);
                    }

                    Server server = new Server(this, connection, weight);
                    server.startHandlingRequests();
                    loadBalancingAlgorithm.addServer(server);
                } catch(IOException ex) {
                    LOGGER.fatal("IOException in Server Accept Handler", ex);
                    System.exit(1);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.fatal("Could not find resource file", e);
            System.exit(1);
        } catch (IOException e) {
            LOGGER.fatal("Could not open socket", e);
            System.exit(1);
        }
    }

    /**
     * Disconnects the given server from this LoadBalancer.
     *
     * @param server the server to disconnect
     */
    public void disconnect(Server server) {
        LOGGER.debug("Disconnection Server " + server.getConnection());
        loadBalancingAlgorithm.removeServer(server);
        server.getConnection().close();
    }

    private class ClientAcceptHandler implements Runnable {

        private boolean run = true;

        @Override
        public void run() {
            LOGGER.info("Waiting for new Client Connections");

            while(run) {

                try {
                    Socket client = clients.accept();
                    new Thread(new RequestHandler(client, loadBalancingAlgorithm)).start();
                    LOGGER.info("New Client Connection");
                } catch(IOException ex) {
                    LOGGER.fatal("IOException in Server Accept Handler", ex);
                    System.exit(1);
                }
            }
        }
    }
}