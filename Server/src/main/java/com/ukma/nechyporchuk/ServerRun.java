package com.ukma.nechyporchuk;

import com.ukma.nechyporchuk.core.utils.Constants;
import com.ukma.nechyporchuk.network.implementation.tcp.StoreServerTCP;
import com.ukma.nechyporchuk.network.implementation.udp.StoreServerUDP;

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
