package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;

/**
 * @author mreilaender
 * @date: 23.02.2016
 */
public abstract class Server {
    private Connection connection;

    protected Server(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
