package com.ukma.stockmanager;

import com.ukma.stockmanager.core.utils.Constants;
import com.ukma.stockmanager.network.implementation.tcp.StoreServerTCP;
import com.ukma.stockmanager.network.implementation.udp.StoreServerUDP;

public class ServerRun {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            StoreServerTCP serverTCP = new StoreServerTCP();
            serverTCP.start(Constants.TCP_PORT);
        });
        thread.start();

        StoreServerUDP serverUDP = new StoreServerUDP();
        serverUDP.start();

        thread.join();
    }
}
