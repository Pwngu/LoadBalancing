package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;

/**
 * mreilaender
 */
public class ResponseTimeServer extends Server {
    private int currentResponseTime;

    public ResponseTimeServer(Connection connection) {
        super(connection);
    }
}
