package at.tgm.ablkreim.client;

import at.tgm.ablkreim.balancer.PiRequest;
import at.tgm.ablkreim.common.connection.Connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public void start() {

        try {

            Connection connection = new Connection(new Socket("localhost", 1235), "Client Connection");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while(true) {
                System.out.print("> ");
                String line = reader.readLine();

                if(line.equals("exit")) break;

                connection.send(new PiRequest(0, Integer.parseInt(line), PiRequest.LEIBNIZ));

                Object response = connection.receive();
                System.out.println(response);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
