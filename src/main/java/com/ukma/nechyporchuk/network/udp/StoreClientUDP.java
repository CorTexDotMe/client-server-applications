package com.ukma.nechyporchuk.network.udp;

import java.io.IOException;
import java.net.*;

public class StoreClientUDP {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    public StoreClientUDP() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public String sendEcho(String msg) {
        try {
            buf = msg.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, 1337);
            socket.send(packet);

            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        socket.close();
    }

    public static void main(String[] args) {
        StoreClientUDP client = new StoreClientUDP();
        String response = client.sendEcho("Hello");
        System.out.println(response);
    }
}
