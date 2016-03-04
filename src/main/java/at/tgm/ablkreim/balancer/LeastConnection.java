package at.tgm.ablkreim.balancer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author: mreilaender
 * @date: 04.03.2016
 */
public class LeastConnection implements LoadBalancingAlgorithm {
    public static final Logger LOGGER = LogManager.getLogger(LeastConnection.class);
    private Set<Server> servers;

    public LeastConnection() {
        this.servers = new TreeSet<>();
    }

    @Override
    public void addServer(Server server) {
        this.servers.add(server);
    }

    @Override
    public void removeServer(Server server) {
        this.servers.remove(server);
    }

    @Override
    public void send(PiRequest piRequest) {
        Server leastConnectionServer = null;
        for (Server s: this.servers) {
            if(leastConnectionServer == null || leastConnectionServer.getActiveConnections() > s.getActiveConnections())
                leastConnectionServer = s;
        }
        if(leastConnectionServer != null)
            leastConnectionServer.sendRequest(piRequest);
        else
            LOGGER.error("Could not find server with least connections, so could not send request. {}", piRequest);
    }
}
