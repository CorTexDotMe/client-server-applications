package com.ukma.stockmanager.network.implementation.udp;

import com.ukma.stockmanager.network.interfaces.Sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSender implements Sender {

    private final DatagramSocket socket;
    private final DatagramPacket datagramPacket;

    public UDPSender(DatagramSocket socket, DatagramPacket datagramPacket) {
        this.socket = socket;
        this.datagramPacket = datagramPacket;
    }

    @Override
    public void sendMessage(byte[] message) {
        try {
            socket.send(new DatagramPacket(message, message.length, datagramPacket.getAddress(), datagramPacket.getPort()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
