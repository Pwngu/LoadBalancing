package at.tgm.ablkreim.balancer;

import java.util.ArrayList;
import java.util.List;

/**
 * Round Robin implementation of LoadBalancing algorithm.
 *
 * @author Klaus Ableitinger
 * @version 03.03.2016
 */
public class RoundRobin implements LoadBalancingAlgorithm {

    private List<Server> servers;
    private int current;

    public RoundRobin() {
        this.servers = new ArrayList<>();
        this.current = 0;
    }

    @Override
    public void addServer(Server server) {
        servers.add(server);
    }

    @Override
    public void removeServer(Server server) {
        if(current >= servers.size()) current = 0;
        servers.remove(server);
    }

    @Override
    public Server send(PiRequest piRequest) {
        if(servers.isEmpty()) throw new RuntimeException("No servers added");

        Server server = servers.get(current++);
        if(current >= servers.size()) current = 0;

        server.sendRequest(piRequest);
        return server;
    }
}
