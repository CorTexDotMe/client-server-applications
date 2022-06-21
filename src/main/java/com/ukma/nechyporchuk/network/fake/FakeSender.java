package com.ukma.nechyporchuk.network.fake;

import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.network.Sender;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class FakeSender implements Sender {
    @Override
    public void sendMessage(byte[] message, InetAddress target) {
        Packet packet = new Packet(message);
        System.out.println(
                packet.getBPktId() + " (bPktId). " +
                new String(packet.getBMsg().getMessage(), StandardCharsets.UTF_8)
        );
    }
}
