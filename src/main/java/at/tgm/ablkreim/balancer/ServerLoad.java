package at.tgm.ablkreim.balancer;

import java.io.Serializable;

/**
 * @author: mreilaender
 * @date: 23.02.2016
 */
public class ServerLoad implements Serializable {
    public final double cpu, ram, drive, network;

    public ServerLoad(double cpu, double ram, double drive, double network) {

        this.cpu = cpu;
        this.ram = ram;
        this.drive = drive;
        this.network = network;
    }
}
