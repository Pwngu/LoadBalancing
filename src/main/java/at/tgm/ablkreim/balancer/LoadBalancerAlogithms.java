package at.tgm.ablkreim.balancer;

/**
 * @author: mreilaender
 * @date: 03.03.2016
 */
public interface LoadBalancerAlogithms {
    int WEIGHTED_DISTRIBUTION = 1;
    int RESPONSE_TIME = 2;
    int LEAST_CONNECTION = 3;
    int SERVER_LOAD = 4;
}
