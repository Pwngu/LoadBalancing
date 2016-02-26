package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;

/**
 * @author: mreilaender
 * @date: 23.02.2016
 */
public class WeightedDistributionServer extends Server {
    private double assessment;

    public WeightedDistributionServer(Connection connection) {
        super(connection);
    }
}
