package at.tgm.ablkreim.balancer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Klaus Ableitinger
 * @version 04.03.2016
 */
public class LeastConnection implements LoadBalancingAlgorithm {

    private List<Server> servers;

    public LeastConnection() {
        this.servers = new ArrayList<>();
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
    public Server send(PiRequest piRequest) {
        if(servers.isEmpty()) throw new RuntimeException("No servers added");

        servers.sort((first, second) -> first.getActiveConnections() - second.getActiveConnections());
        Server server = servers.get(servers.size() - 1);

        server.sendRequest(piRequest);
        return server;
    }
}
