package com.ukma.nechyporchuk.network.implementation.udp;

import com.ukma.nechyporchuk.network.interfaces.Sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSender implements Sender {

    private DatagramSocket socket;
    private DatagramPacket datagramPacket;

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
