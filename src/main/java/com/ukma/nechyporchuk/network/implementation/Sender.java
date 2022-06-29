package com.ukma.nechyporchuk.network.implementation;

import com.ukma.nechyporchuk.core.Packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Print packet message in console instead of sending packet.
 */
public class Sender implements com.ukma.nechyporchuk.network.interfaces.Sender {
    private final DataOutputStream out;

    public Sender(DataOutputStream out) {
        this.out = out;
    }

    @Override
    public void sendMessage(byte[] message, InetAddress target) {
        try {
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
