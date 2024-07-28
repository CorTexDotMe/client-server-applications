package com.ukma.stockmanager.network.implementation.tcp;

import java.io.DataOutputStream;
import java.io.IOException;

public class TCPSender implements com.ukma.stockmanager.network.interfaces.Sender {
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
