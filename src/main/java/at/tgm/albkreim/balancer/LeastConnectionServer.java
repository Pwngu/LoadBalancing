package at.tgm.albkreim.balancer;

import at.tgm.albkreim.common.Connection;

/**
 * mreilaender
 */
public class LeastConnectionServer extends Server {
    /* TODO Maybe not just the number of active connections, but a List with concrete Instances of all connections */
    private int activeConnections;

    public LeastConnectionServer(Connection connection) {
        super(connection);
    }
}
