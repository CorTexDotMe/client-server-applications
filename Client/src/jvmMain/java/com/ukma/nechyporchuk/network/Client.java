package com.ukma.nechyporchuk.network;

public interface Client {
    byte[] sendAndReceiveMessage(byte[] msg);
    byte[] sendAndReceiveMessageWithoutReconnect(byte[] msg);

    void startConnection(String ip, int port, boolean reconnect);

    void endConnection();

    int getPort();
}
