package at.tgm.ablkreim.balancer;

import java.io.Serializable;

/**
 * @author: mreilaender
 * @date: 23.02.2016
 */
public class PiRequest implements Serializable {

    public static int LEIBNIZ = 1;

    public final int precision, begin, end, algorithmn;
    public final long id;

    public PiRequest(int precision, int begin, int end, int algorithmn) {
        this.id = System.currentTimeMillis();
        this.precision = precision;
        this.begin = begin;
        this.end = end;
        this.algorithmn = algorithmn;
    }
}
