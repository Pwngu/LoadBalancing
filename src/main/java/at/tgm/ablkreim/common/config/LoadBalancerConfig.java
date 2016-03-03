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

        return (Integer) super.config.get("serverPort");
    }

    public String getIP() {

        return (String) super.config.get("ip");
    }

    public int getPort() {

        return (Integer) super.config.get("port");
    }

    public int getPiAlgorithm() {

        return (Integer) super.config.get("piAlgorithm");
    }

    public int getLoadBalancerAlgorithm() {

        return (Integer) super.config.get("loadBalancerAlgorithm");
    }
}
