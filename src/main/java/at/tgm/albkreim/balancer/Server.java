package at.tgm.albkreim.balancer;

import at.tgm.albkreim.common.Connection;

/**
 * @author mreilaender
 * @date: 23.02.2016
 */
public abstract class Server {
    private Connection connection;

    public Server(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
