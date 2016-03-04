package at.tgm.ablkreim.balancer;

import at.tgm.ablkreim.common.connection.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author mreilaender
 * @version 04.03.2016
 */
public class AcceptHandler implements Runnable {
    private boolean running = true;
    private ServerSocket serverSocket;
    private LoadBalancingAlgorithm loadBalancingAlgorithm;

    public AcceptHandler(ServerSocket serverSocket, LoadBalancingAlgorithm loadBalancingAlgorithm) {
        this.serverSocket = serverSocket;
        this.loadBalancingAlgorithm = loadBalancingAlgorithm;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket client = serverSocket.accept();
                new Thread(new RequestHandler(client, this.loadBalancingAlgorithm)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
