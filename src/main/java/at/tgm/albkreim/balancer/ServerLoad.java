package at.tgm.albkreim.balancer;

import java.io.Serializable;

/**
 * @author: mreilaender
 * @date: 23.02.2016
 */
public class ServerLoad implements Serializable {
    private double cpu, ram, drive, network;

    public double getCpu() {
        return cpu;
    }

    public double getRam() {
        return ram;
    }

    public double getDrive() {
        return drive;
    }

    public double getNetwork() {
        return network;
    }
}
