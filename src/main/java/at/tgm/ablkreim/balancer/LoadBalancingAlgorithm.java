package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.balancer.Server;

/**
 *
 *
 * @author Manuel Reiländer
 * @version 03.03.2016
 */
public interface LoadBalancingAlgorithm {

    /** Constant for the round robin balancing algorithm configuration value */
    int ROUND_ROBIN = 0;

    /** Constant for the weighted distribution balancing algorithm configuration value */
    int WEIGHTED_DISTRIBUTION = 1;

    /** Constant for the response time balancing algorithm configuration value */
    int RESPONSE_TIME = 2;

    /** Constant for the least connection balancing algorithm configuration value */
    int LEAST_CONNECTION = 3;

    /** Constant for the server probes balancing algorithm configuration value */
    int SERVER_PROBES = 4;

    /**
     * Adds a server to this balancing algorithm.
     *
     * @param server the server to add
     */
    public void addServer(Server server);

    /**
     * Removes a server from this balancing algorithm.
     *
     * @param server the server to remove
     */
    public void removeServer(Server server);

    /**
     * Sends the given request to the next server
     * based on the underlying balancing integration.
     *
     * @param piRequest the request to send
     * @return the server the request was sent to
     */
    public Server send(PiRequest piRequest);
}
