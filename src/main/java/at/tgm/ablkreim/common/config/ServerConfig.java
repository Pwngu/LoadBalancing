package at.tgm.ablkreim.common.config;

import java.io.Reader;

public class ServerConfig extends Config {

    public ServerConfig(Reader in) {
        super(in);
    }

    public String getIP() {

        return (String) super.config.get("ip");
    }

    public int getPort() {

        return (int) super.config.get("port");
    }
}
