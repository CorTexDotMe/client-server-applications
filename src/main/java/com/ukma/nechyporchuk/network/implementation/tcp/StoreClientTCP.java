package com.ukma.nechyporchuk.network.implementation.tcp;

import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.network.interfaces.Receiver;
import com.ukma.nechyporchuk.utils.Constants;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class StoreClientTCP {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public void sendMessage(byte[] msg) throws IOException {
        out.write(msg);
    }

    public void sendEndMessage() throws IOException {
        sendMessage(new byte[]{Constants.bEnd});
    }

    public String receiveMessage() {
        Packet packet = new Packet(readPacket());

        return packet.getBPktId() + " (bPktId). " +
               new String(packet.getBMsg().getMessage(), StandardCharsets.UTF_8);
    }

    private byte[] readPacket() {
        try {
            byte bMagic;
            do {
                bMagic = in.readByte();             // Trying to find magic byte in order to start reading packet
            } while (bMagic != Constants.bMagic);
            byte bSrc = in.readByte();
            long bPktId = in.readLong();
            int wLen = in.readInt();
            short wCrc16_first = in.readShort();
            ByteBuffer packet = ByteBuffer.wrap(new byte[
                    Constants.BYTES_AMOUNT_FOR_FIRST_CHECKSUM +
                    Constants.BYTES_AMOUNT_OF_CRC +
                    wLen +
                    Constants.BYTES_AMOUNT_OF_CRC
                    ]);
            packet.put(bMagic).put(bSrc).putLong(bPktId).putInt(wLen).putShort(wCrc16_first);
            packet.put(in.readNBytes(wLen));
            short wCrc16_second = in.readShort();
            packet.putShort(wCrc16_second);


            return packet.array();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        StoreClientTCP tcp = new StoreClientTCP();
//        tcp.task();

        for (int i = 0; i < 100; i++)
            threadPool.execute(tcp::task2);
    }

    private void task1() {
        try {
            StoreClientTCP client = new StoreClientTCP();
//        client.startConnection("127.0.0.1", 1337);
            client.startConnection("127.0.0.1", 6666);
            client.sendMessage(Receiver.getRandomPacket());
            client.sendMessage(Receiver.getRandomPacket());
            client.sendMessage(new byte[]{Constants.bEnd});

//        String end = client.sendMessage(".".getBytes());
            String response2 = client.receiveMessage();
            String response1 = client.receiveMessage();
            String responseEnd = client.receiveMessage();

            System.out.println(response1);
            System.out.println(response2);
            System.out.println(responseEnd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void task2() {
        try {
            StoreClientTCP client1 = new StoreClientTCP();
            client1.startConnection("127.0.0.1", 6666);
            client1.sendMessage(Receiver.getRandomPacket());

            StoreClientTCP client2 = new StoreClientTCP();
            client2.startConnection("127.0.0.1", 6666);
            client2.sendMessage(Receiver.getRandomPacket());


            String response1 = client1.receiveMessage();
            String response2 = client2.receiveMessage();
            System.out.println(response1);
            System.out.println(response2);

            client1.sendEndMessage();
            client2.sendEndMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
