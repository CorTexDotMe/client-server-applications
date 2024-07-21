package com.ukma.stockmanager.network.udp;

import com.ukma.stockmanager.core.entities.Packet;
import com.ukma.stockmanager.core.utils.Constants;
import com.ukma.stockmanager.network.Client;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class StoreClientUDP implements Client {
    private DatagramSocket socket;
    private InetAddress address;
    private int port = Constants.UDP_PORT;

    private byte[] buf;

    public StoreClientUDP() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * Just send the message
     *
     * @param msg  - byte array as message
     * @param port - integer as port for datagram packet
     */
    public void sendMessage(byte[] msg, int port) {
        try {
            buf = msg;
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Just waiting and receiving the message. Stop only because of exception.
     *
     * @param port - integer as port for datagram packet
     * @return message: packetId. CType
     */
    public byte[] receiveMessage(int port) {
        try {
            buf = new byte[Constants.MAX_PACKET_LENGTH];
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.setSoTimeout(Constants.WAITING_TIME_MILLISECONDS);
            socket.receive(packet);

            return packet.getData();
        } catch (SocketTimeoutException e) {
            System.out.println("Sending packet again"); // for testing
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Can send message and then wait to receive it. If no message is received within
     * Constants.WAITING_TIME_MILLISECONDS, it will send message once again.
     * Create this cycle until response is received.
     *
     * @param msg - byte array as message
     * @return byte array
     */
    public byte[] sendAndReceiveMessage(byte[] msg) {
        return sendAndReceive(msg, true);
    }

    public byte[] sendAndReceiveMessageWithoutReconnect(byte[] msg) {
        return sendAndReceive(msg, false);
    }

    private byte[] sendAndReceive(byte[] msg, boolean reconnect) {
        try {
            sendMessage(msg, port);

            buf = new byte[Constants.MAX_PACKET_LENGTH];
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

            socket.setSoTimeout(Constants.WAITING_TIME_MILLISECONDS);
            socket.receive(packet);

            return packet.getData();
        } catch (SocketTimeoutException e) {
            System.out.println("Sending packet again"); // for testing

            if (reconnect)
                return sendAndReceiveMessage(msg);
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void startConnection(String ip, int port, boolean reconnect) {
        this.port = port;
    }

    @Override
    public void endConnection() {

    }

    private String decryptPacket(byte[] packet) {
        Packet responsePacket = new Packet(packet);
        return responsePacket.getBPktId() + " (bPktId). " +
               new String(responsePacket.getBMsg().getMessage(), StandardCharsets.UTF_8);
    }

    public void close() {
        socket.close();
    }

    public int getPort() {
        return port;
    }

    /*
    public static void main(String[] args) {
        StoreClientUDP client = new StoreClientUDP();
        client.sendMessage(Receiver.getRandomPacket(), Constants.UDP_PORT);
        String response = client.receiveMessage(Constants.UDP_PORT);
        System.out.println(response);
    }
     */
}
