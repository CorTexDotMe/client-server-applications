package com.ukma.nechyporchuk.network.implementation.udp;

import com.ukma.nechyporchuk.core.Controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;

public class StoreServerUDP extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public StoreServerUDP() {
        try {
            socket = new DatagramSocket(1337);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                socket.receive(datagramPacket);

                Controller.getInstance().workWithUDPPacket(socket, datagramPacket).get();


                InetAddress address = datagramPacket.getAddress();
                int port = datagramPacket.getPort();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
                String received
                        = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("end")) {
                    running = false;
//                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        socket.close();
    }

    public static void main(String[] args) {
        StoreServerUDP server = new StoreServerUDP();
        server.start();

    }
}
