package at.tgm.ablkreim.balancer;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Klaus Ableitinger
 * @version 04.03.2016
 */
public class LeastConnection implements LoadBalancingAlgorithm {

    private List<Server> servers = new ArrayList<>();

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
        if(servers.isEmpty()) throw new InvalidStateException("No servers added");

        servers.sort((first, second) -> first.getActiveConnections() - second.getActiveConnections());
        Server server = servers.get(servers.size() - 1);

        server.sendRequest(piRequest);
        return server;
    }
}
