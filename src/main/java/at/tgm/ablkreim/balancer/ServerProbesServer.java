package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;

/**
 * @author: mreilaender
 * @date: 23.02.2016
 */
public class ServerProbesServer extends Server {
    private Connection loadConnection;
    private ServerLoad load;

    public ServerProbesServer(Connection connection) {
        super(connection);
    }
}
