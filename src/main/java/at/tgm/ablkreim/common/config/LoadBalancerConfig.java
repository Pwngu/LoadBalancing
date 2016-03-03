package at.tgm.ablkreim.common.config;

import java.io.Reader;

public class LoadBalancerConfig extends Config {

    public LoadBalancerConfig(Reader in) {
        super(in);
    }

    public String getServIP() {

        return (String) super.config.get("servip");
    }

    public int getServPort() {

        return (Integer) super.config.get("servport");
    }

    public String getIP() {

        return (String) super.config.get("ip");
    }

    public int getPort() {

        return (Integer) super.config.get("port");
    }

    public int getPiAlgorithm() {

        return (Integer) super.config.get("pialgorithm");
    }

    public int getLbAlgorithm() {

        return (Integer) super.config.get("lbalgorithm");
    }
}
