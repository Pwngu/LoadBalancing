package at.tgm.ablkreim;

import at.tgm.ablkreim.balancer.LoadBalancer;
import at.tgm.ablkreim.client.Client;
import at.tgm.ablkreim.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Run {

    private static final Logger LOGGER = LogManager.getLogger(Run.class);

    private Run() { }

    public static void main(String[] args) {

        if(args == null || args.length < 1) {
            LOGGER.fatal("Illegal number Arguments");
            System.exit(1);
        }

        switch(args[0]) {
            case "server":
                int weight = 0;
                if(args.length == 2) {
                    try { weight = Integer.parseInt(args[1]);
                    } catch(NumberFormatException ex) {
                        LOGGER.warn("Invalid second argument");
                    }
                }
                new Server(weight).start();
                break;
            case "balancer":
                new LoadBalancer().start();
                break;
            case "client":
                new Client().start();
                break;
            default:
                LOGGER.fatal("Illegal first argument");
                System.exit(1);
        }
    }
}
