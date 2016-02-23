package at.tgm.albkreim.balancer;

import at.tgm.albkreim.common.Connection;

/**
 * mreilaender
 */
public class ResponseTimeServer extends Server {
    private int currentResponseTime;

    public ResponseTimeServer(Connection connection) {
        super(connection);
    }
}
