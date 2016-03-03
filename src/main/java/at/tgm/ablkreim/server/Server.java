package at.tgm.ablkreim.server;

import at.tgm.ablkreim.balancer.PiRequest;
import at.tgm.ablkreim.balancer.PiResponse;
import at.tgm.ablkreim.common.config.ServerConfig;
import at.tgm.ablkreim.common.connection.Connection;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * @author: mreilaender
 * @date: 26.02.2016
 */
public class Server {

    private static final Logger LOG = LogManager.getLogger(Server.class);

    public static void main(String[] args) {
        new Server().start();
    }


    private ServerConfig config;

    public Server() {

        try {

            URL url = getClass().getClassLoader().getResource("serv_config:json");
            if(url == null)
                LOG.fatal("Cannot find config file");
                System.exit(1);

            config = new ServerConfig(new FileReader(url.getFile()));
        } catch(FileNotFoundException ex) {

            LOG.fatal("Cannot find config file", ex);
            System.exit(1);
        }
    }

    public void start() {

    }

    private BigDecimal calculateLeibniz(int begin, int end) {
        BigDecimal result = new BigDecimal(0.0);
        for(; begin <= end; ++begin) {

            result = result.add(new BigDecimal(Math.pow(-1, begin) / (2 * begin + 1)));
        }
        return result.multiply(new BigDecimal(4));
    }

    private class HandlerThread implements Runnable {

        private boolean running = true;
        private Connection connection;

        @Override
        public void run() {

            LOG.info("Starting server handler");
            try {

                connection = new Connection(new Socket(config.getIP(), config.getPort()), "LoadBalancerConnection");

                while(running) {

                    PiRequest request = connection.receive();

                    if(request == null) {

                        LOG.error("Received invalid Object");
                        continue;
                    }

                    connection.send(new PiResponse(calculateLeibniz(request.begin, request.end), request.id));
                }
            } catch(Exception ex) {

                LOG.fatal("Exception in HandlerThread", ex);
            }
        }

        public void stop() {

            running = false;
        }
    }
}