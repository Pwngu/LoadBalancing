package at.tgm.ablkreim.common.config;

import java.io.Reader;

public class LoadBalancerConfig extends Config {

    public LoadBalancerConfig(Reader in) {
        super(in);
    }

    public String getServerIP() {

        return (String) super.config.get("serverIp");
    }

    public int getServerPort() {

        return ((Long) super.config.get("serverPort")).intValue();
    }

    public String getIP() {

        return (String) super.config.get("ip");
    }

    public int getPort() {

        return ((Long) super.config.get("port")).intValue();
    }

    public int getPiAlgorithm() {

        return ((Long) super.config.get("piAlgorithm")).intValue();
    }

    public int getLoadBalancerAlgorithm() {

        return ((Long) super.config.get("lbAlgorithm")).intValue();
    }
}
