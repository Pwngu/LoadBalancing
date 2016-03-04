package at.tgm.ablkreim.balancer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author: mreilaender
 * @date: 03.03.2016
 */
public class WeightedDistribution implements LoadBalancingAlgorithm {
    public static final Logger LOGGER = LogManager.getLogger(WeightedDistribution.class);

    public WeightedDistribution() { }

    @Override
    public void addServer(Server server) {
        throw new NotImplementedException();
    }

    @Override
    public void removeServer(Server server) {
        throw new NotImplementedException();
    }

    @Override
    public void send(PiRequest piRequest) {
        throw new NotImplementedException();
    }
}
