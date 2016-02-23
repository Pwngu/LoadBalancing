package at.tgm.albkreim.balancer;

import at.tgm.albkreim.common.Connection;

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
