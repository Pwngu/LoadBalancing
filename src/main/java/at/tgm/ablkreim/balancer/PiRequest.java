package at.tgm.ablkreim.balancer;

import java.io.Serializable;

/**
 * @author: mreilaender
 * @date: 23.02.2016
 */
public class PiRequest implements Serializable {
    /* TODO Maybe rename to algorithm, then making an interface with constant variables describing the algorithm (e.g. Algorithms interface) */
    public static int LEIBNIZ = 1;

    private int precision, begin, end, algorithmn;
}
