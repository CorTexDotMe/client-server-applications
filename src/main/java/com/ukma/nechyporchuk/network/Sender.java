package com.ukma.nechyporchuk.network;

import java.net.InetAddress;

public interface Sender {
    void sendMessage(byte[] message, InetAddress target);
}
