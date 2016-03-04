package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.balancer.Server;

import java.util.ArrayList;
import java.util.List;

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
    public void send(PiRequest piRequest) {
        if(servers.isEmpty()) return;

        servers.get(current++).sendRequest(piRequest);
        if(current >= servers.size()) current = 0;
    }
}
