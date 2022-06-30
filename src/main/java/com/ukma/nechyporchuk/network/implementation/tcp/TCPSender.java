package com.ukma.nechyporchuk.network.implementation.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Print packet message in console instead of sending packet.
 */
public class TCPSender implements com.ukma.nechyporchuk.network.interfaces.Sender {
    private final DataOutputStream out;

    public TCPSender(DataOutputStream out) {
        this.out = out;
    }

    @Override
    public void sendMessage(byte[] message) {
        try {
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
