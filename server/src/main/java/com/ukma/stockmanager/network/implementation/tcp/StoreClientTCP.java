package com.ukma.stockmanager.network.implementation.tcp;

import com.ukma.stockmanager.core.entities.Packet;
import com.ukma.stockmanager.core.utils.Constants;
import com.ukma.stockmanager.network.interfaces.Receiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StoreClientTCP {
    private Socket clientSocket;
    private String ip;
    private int port;

    private DataOutputStream out;
    private DataInputStream in;

    public void startConnection(String ip, int port) {
        try {
            this.ip = ip;
            this.port = port;

            clientSocket = new Socket(ip, port);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (ConnectException e) {
            try {
//              Client is trying to reconnect
                Thread.sleep(Constants.WAITING_TIME_MILLISECONDS);
                startConnection(ip, port);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(byte[] msg) throws IOException {
        out.write(msg);
    }

    public void sendEndMessage() throws IOException {
        sendMessage(Constants.bEndMessage);
    }

    public String receiveMessage() {
        try {
            return decryptPacket(readPacket());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] receiveMessageBytes() {
        try {
            return readPacket();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] readPacket() throws IOException {
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
    }

    public String sendAndReceiveMessage(byte[] msg) {
        try {
            out.write(msg);

            return decryptPacket(readPacket());
        } catch (IOException e) {
            try {
//              Client is trying to reconnect
                Thread.sleep(Constants.WAITING_TIME_MILLISECONDS);
                startConnection(ip, port);
                return sendAndReceiveMessage(msg);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    private String decryptPacket(byte[] packet) {
        Packet responsePacket = new Packet(packet);
        return responsePacket.getBPktId() + " (bPktId). " +
               new String(responsePacket.getBMsg().getMessage(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws IOException {
        StoreClientTCP client = new StoreClientTCP();
        client.startConnection("127.0.0.1", Constants.TCP_PORT);
        client.sendMessage(Receiver.getRandomPacket());
        client.sendMessage(Receiver.getRandomPacket());
        client.sendEndMessage();

        String response2 = client.receiveMessage();
        String response1 = client.receiveMessage();
        String responseEnd = client.receiveMessage();

        System.out.println(response1);
        System.out.println(response2);
        System.out.println(responseEnd);
    }

}
