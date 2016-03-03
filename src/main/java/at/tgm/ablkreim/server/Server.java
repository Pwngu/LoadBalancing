package at.tgm.ablkreim.server;

import at.tgm.ablkreim.common.connection.Connection;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author: mreilaender
 * @date: 26.02.2016
 */
public class Server {
    private Connection connection;
    private final Logger LOG = LogManager.getLogger(Server.class);
    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        /* simple-json way */
        JSONParser parser = new JSONParser();
        try {
            try {
                JSONObject tmp = (JSONObject)((JSONObject)parser.parse(new FileReader(this.getClass().getClassLoader().getResource("config.json").getFile()))).get("server");
                Set<Map.Entry<String, JsonElement>> set = tmp.entrySet();
                for(Map.Entry<String, JsonElement> item : set) {
                    //System.out.println(tmp.get(item.getKey()));
                }
                this.LOG.info("Test");
                System.out.println(this.getClass().getCanonicalName());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                System.out.println("No such file");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private double calculateLeibniz(int begin, int end) {
        double result = 0.0;
        for(;begin <= end;++begin) {
            result += (Math.pow(-1, begin))/(2*begin+1);
        }
        return result;
    }
}