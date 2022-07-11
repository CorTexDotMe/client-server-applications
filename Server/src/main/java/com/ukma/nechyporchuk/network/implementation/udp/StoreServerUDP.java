package com.ukma.nechyporchuk.network.implementation.udp;

import com.ukma.nechyporchuk.utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class StoreServerUDP extends Thread {
    private DatagramSocket socket;
    private boolean running = true;
    private final byte[] buf = new byte[Constants.MAX_PACKET_LENGTH];

    public StoreServerUDP() {
        try {
            socket = new DatagramSocket(Constants.UDP_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (running) {
//            new Thread(() -> {
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    socket.receive(datagramPacket);
                    UDPReceiver receiver = new UDPReceiver(socket, datagramPacket);
                    receiver.receiveMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }).start();
        }
        socket.close();
    }

    public void stopServer() {
        running = false;
    }

    public static void main(String[] args) {
        StoreServerUDP server = new StoreServerUDP();
        server.start();

    }
}
