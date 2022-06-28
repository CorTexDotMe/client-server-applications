package com.ukma.nechyporchuk.network.interfaces;

import java.net.InetAddress;

public interface Sender {
    void sendMessage(InetAddress target);
}
