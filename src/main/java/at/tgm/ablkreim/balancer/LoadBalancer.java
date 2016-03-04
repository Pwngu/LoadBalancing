package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.config.LoadBalancerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
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

    /**
     * Main method starting a LoadBalancer instance.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        new LoadBalancer().start();
    }

    /**
     * Default LoadBalancer constructor.
     */
    public LoadBalancer() {
        this.connectedServers = new ArrayList<>();
        try {
            URL url = getClass().getClassLoader().getResource("loadBalancer_config.json");
            if (url == null)
                LOGGER.fatal("Cannot find config file");
            System.exit(1);
            loadBalancerConfig = new LoadBalancerConfig(new FileReader(url.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.fatal("Could not find file", e);
        }
        switch (loadBalancerConfig.getLoadBalancerAlgorithm()) {
            case LoadBalancingAlgorithm.WEIGHTED_DISTRIBUTION:
                throw new NotImplementedException();
            case LoadBalancingAlgorithm.LEAST_CONNECTION:
                this.loadBalancingAlgorithm = new LeastConnection();
                break;
            case LoadBalancingAlgorithm.RESPONSE_TIME:
                this.loadBalancingAlgorithm = new RoundRobin();
                break;
            case LoadBalancingAlgorithm.SERVER_PROBES:
                throw new NotImplementedException();
        }
    }

    /**
     * Starts the LoadBalancer listening for server and client connections.
     */
    public void start() {
        try {
            LOGGER.debug("Reading connfig");
            InetAddress serverHost = InetAddress.getByName(loadBalancerConfig.getServerIP());
            InetAddress clientHost = InetAddress.getByName(loadBalancerConfig.getIP());

            LOGGER.debug("Creating ServerSockets");
            this.servers = new ServerSocket(loadBalancerConfig.getServerPort(), 50, serverHost);
            this.clients = new ServerSocket(loadBalancerConfig.getPort(), 50, clientHost);

            LOGGER.debug("Waiting for new connections");
            Thread waitForConnections = new Thread(new AcceptHandler(this.clients, this.loadBalancingAlgorithm));
            waitForConnections.start();
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
        loadBalancingAlgorithm.removeServer(server);
        server.getConnection().close();
    }
}