package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.server.Server;

/**
 * @author: mreilaender
 * @date: 03.03.2016
 */
public interface LoadBalancingAlgorithm {
    /* Load Balancer Algorithms */
    int WEIGHTED_DISTRIBUTION = 1;
    int RESPONSE_TIME = 2;
    int LEAST_CONNECTION = 3;
    int SERVER_LOAD = 4;

    public void addServer(Server server);
    public void removeServer(Server server);
    public void send(PiRequest piRequest);
}
