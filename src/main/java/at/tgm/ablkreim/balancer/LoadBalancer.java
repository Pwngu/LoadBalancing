package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.config.LoadBalancerConfig;
import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Manuel Reiländer
 * @version 03.03.2016
 */
public class LoadBalancer {
    private static final Logger LOG = LogManager.getLogger(LoadBalancer.class);

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
                LOG.fatal("Cannot find config file");
            System.exit(1);
            loadBalancerConfig = new LoadBalancerConfig(new FileReader(url.getFile()));
        } catch (FileNotFoundException e) {
            LOG.fatal("Could not find file", e);
        }
        switch (loadBalancerConfig.getLoadBalancerAlgorithm()) {
            case LoadBalancingAlgorithm.WEIGHTED_DISTRIBUTION:
                this.loadBalancingAlgorithm = new WeightedDistribution();
                break;
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
            LOG.info("Reading connfig");
            InetAddress serverHost = InetAddress.getByName(loadBalancerConfig.getServerIP());
            InetAddress clientHost = InetAddress.getByName(loadBalancerConfig.getIP());

            LOG.info("Creating ServerSockets");
            this.servers = new ServerSocket(loadBalancerConfig.getServerPort(), 50, serverHost);
            this.clients = new ServerSocket(loadBalancerConfig.getPort(), 50, clientHost);

            LOG.info("Waiting for new connections");
            Thread waitForConnections = new Thread(new AcceptHandler());
            waitForConnections.start();
        } catch (FileNotFoundException e) {
            LOG.fatal("Could not find resource file", e);
            System.exit(1);
        } catch (IOException e) {
            LOG.fatal("Could not open socket", e);
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

    private class AcceptHandler implements Runnable {

        private boolean running = true;
        private Connection connection;

        @Override
        public void run() {
            while (running) {
                try {
                    Socket client = clients.accept();
                    new Thread(new RequestHandler(client)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }

    private class RequestHandler implements Runnable {
        private Socket client;
        private Connection conection;
        private boolean running;

        public RequestHandler(Socket client) {
            this.client = client;
            this.running = true;
        }

        @Override
        public void run() {
            this.conection = new Connection(this.client, this.client.getRemoteSocketAddress().toString());
            loadBalancingAlgorithm.send(this.conection.receive());
            while(true);
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }
}