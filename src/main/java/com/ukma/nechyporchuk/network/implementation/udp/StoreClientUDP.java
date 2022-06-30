package com.ukma.nechyporchuk.network.implementation.udp;

import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.network.interfaces.Receiver;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

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

    public String sendMessage(byte[] msg, int port) {
        try {
            buf = msg;
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);

            buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            Packet responsePacket = new Packet(packet.getData());

            return responsePacket.getBPktId() + " (bPktId). " +
                   new String(responsePacket.getBMsg().getMessage(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        socket.close();
    }

    public static void main(String[] args) {
        //Todo port as constant value

        StoreClientUDP client = new StoreClientUDP();
        String response = client.sendMessage(Receiver.getRandomPacket(), 1337);
        System.out.println(response);
    }
}
