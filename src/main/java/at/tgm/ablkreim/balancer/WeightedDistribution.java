package at.tgm.ablkreim.balancer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * @author: mreilaender
 * @date: 03.03.2016
 */
public class WeightedDistribution implements LoadBalancingAlgorithm {
    public static final Logger LOGGER = LogManager.getLogger(WeightedDistribution.class);

    private ArrayList<Server> servers;

    public WeightedDistribution() {

        servers = new ArrayList<>();
    }

    @Override
    public void addServer(Server server) {
        servers.add(server);
    }

    @Override
    public void removeServer(Server server) {
        servers.remove(server);
    }

    @Override
    public Server send(PiRequest piRequest) {

        // Compute the total weight of all items together
        int totalWeight = 0;
        for (Server server : servers)
            totalWeight += server.getWeight();

        // Now choose a random item
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < servers.size(); ++i) {
            random -= servers.get(i).getWeight();
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }

        Server server = servers.get(randomIndex);
        server.sendRequest(piRequest);
        return server;
    }
}
