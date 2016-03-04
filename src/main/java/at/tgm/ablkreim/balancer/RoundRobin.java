package at.tgm.ablkreim.balancer;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Round Robin implementation of LoadBalancing algorithm.
 *
 * @author Klaus Ableitinger
 * @version 03.03.2016
 */
public class RoundRobin implements LoadBalancingAlgorithm {

    private List<Server> servers = new ArrayList<>();
    private int current = 0;

    @Override
    public void addServer(Server server) {
        servers.add(server);
    }

    @Override
    public void removeServer(Server server) {
        current--;
        servers.remove(server);
    }

    @Override
    public Server send(PiRequest piRequest) {
        if(servers.isEmpty()) throw new InvalidStateException("No servers added");

        Server server = servers.get(current++);
        if(current >= servers.size()) current = 0;

        server.sendRequest(piRequest);
        return server;
    }
}
