package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.config.LoadBalancerConfig;
import at.tgm.ablkreim.common.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: mreilaender
 * @date: 03.03.2016
 */
public class LoadBalancer {
    private static final Logger LOG = LogManager.getLogger(LoadBalancer.class);

    private LoadBalancerConfig loadBalancerConfig;
    private LoadBalancingAlgorithm loadBalancingAlgorithm;
    private ServerSocket servers, clients;
    private List<Server> connectedServers;

    public static void main(String[] args) {
        new LoadBalancer().start();
    }

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
                // TODO
                break;
            case LoadBalancingAlgorithm.RESPONSE_TIME:
                // TODO
                break;
            case LoadBalancingAlgorithm.SERVER_LOAD:
                // TODO
                break;
        }
    }

    public void start() {
        try {
            LOG.info("Reading connfig");
            InetAddress serverHost = InetAddress.getByName(loadBalancerConfig.getServerIP());
            InetAddress clientHost = InetAddress.getByName(loadBalancerConfig.getIP());

            LOG.info("Creating ServerSockets");
            this.servers = new ServerSocket(loadBalancerConfig.getServerPort(), 50, serverHost);
            this.clients = new ServerSocket(loadBalancerConfig.getPort(), 50, clientHost);

            LOG.info("Waiting for new connections");
            Thread waitForConnections = new Thread(new HandlerThread());
            waitForConnections.start();
        } catch (FileNotFoundException e) {
            LOG.fatal("Could not find resource file", e);
            System.exit(1);
        } catch (IOException e) {
            LOG.fatal("Could not open socket", e);
            System.exit(1);
        }
    }

    public void disconnect(Server server) {
        loadBalancingAlgorithm.removeServer(server);
    }

    private class HandlerThread implements Runnable {

        private boolean running = true;
        private Connection connection;

        @Override
        public void run() {
            while (running) {

            }
        }

        public void stop() {
            running = false;
        }
    }
}