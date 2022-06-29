package com.ukma.nechyporchuk.network.tcp;

import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.network.implementation.Receiver;
import com.ukma.nechyporchuk.utils.Constants;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StoreClientTCP {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    public String sendMessage(byte[] msg) throws IOException {
        out.write(msg);

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
        StoreClientTCP client = new StoreClientTCP();
//        client.startConnection("127.0.0.1", 1337);
        client.startConnection("127.0.0.1", 6666);
        String response1 = client.sendMessage(Receiver.getRandomPacket());
//        String response2 = client.sendMessage(Receiver.getRandomPacket());
//        String end = client.sendMessage(".".getBytes());
        System.out.println(response1);
    }
}
