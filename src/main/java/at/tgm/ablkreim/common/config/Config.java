package at.tgm.ablkreim.common.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;

public abstract class Config {

    private static final Logger LOG = LogManager.getLogger(Config.class);


    protected JSONObject config;

    protected Config(Reader in) {

        JSONParser parser = new JSONParser();
        try {

            config = (JSONObject) parser.parse(in);
        } catch(IOException ex) {

            LOG.fatal("Cannot load config file", ex);
            System.exit(1);
        } catch(ParseException ex) {

            LOG.fatal("Cannot parse config file", ex);
            System.exit(1);
        }
    }
}
