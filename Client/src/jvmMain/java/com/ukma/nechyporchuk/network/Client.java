package com.ukma.nechyporchuk.network;

public interface Client {
    byte[] sendAndReceiveMessage(byte[] msg);
    void startConnection(String ip, int port);
    void endConnection();
}
