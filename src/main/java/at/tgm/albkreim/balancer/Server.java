package at.tgm.albkreim.balancer;

import at.tgm.albkreim.common.Connection;

/**
 * mreilaender
 */
public abstract class Server {
    private Connection connection;

    public Server(Connection connection) {
        this.connection = connection;
    }
}
