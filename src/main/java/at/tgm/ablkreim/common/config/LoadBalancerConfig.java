package at.tgm.ablkreim.common.config;

import java.io.Reader;

public class LoadBalancerConfig extends Config {

    public LoadBalancerConfig(Reader in) {
        super(in);
    }

    public String getServIP() {

        return (String) super.config.get("server_ip");
    }

    public int getServPort() {

        return (Integer) super.config.get("server_port");
    }

    public String getIP() {

        return (String) super.config.get("ip");
    }

    public int getPort() {

        return (Integer) super.config.get("port");
    }

    public String getAlgorithm() {

        return (String) super.config.get("algorithm");
    }
}
