package at.tgm.albkreim.balancer;

import at.tgm.albkreim.common.Connection;

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
