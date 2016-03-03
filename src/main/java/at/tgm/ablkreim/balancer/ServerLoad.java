package at.tgm.ablkreim.balancer;

import java.io.Serializable;

/**
 * @author: mreilaender
 * @date: 23.02.2016
 */
public class ServerLoad implements Serializable,Comparable<ServerLoad> {
    public final double cpu, ram, drive, network;

    public ServerLoad(double cpu, double ram, double drive, double network) {
        this.cpu = cpu;
        this.ram = ram;
        this.drive = drive;
        this.network = network;
    }

    @Override
    public int compareTo(ServerLoad other) {
        return (int)((this.cpu + this.ram + this.drive + this.network) - (other.cpu + other.ram + other.drive + other.network));
    }
}
