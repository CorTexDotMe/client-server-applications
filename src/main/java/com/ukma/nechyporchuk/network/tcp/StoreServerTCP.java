package com.ukma.nechyporchuk.network.tcp;

import com.ukma.nechyporchuk.core.Controller;
import com.ukma.nechyporchuk.utils.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StoreServerTCP {
    private ServerSocket serverSocket;

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataOutputStream out;
        private DataInputStream in;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                out = new DataOutputStream(clientSocket.getOutputStream());
                in = new DataInputStream(clientSocket.getInputStream());

//              Read packet
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

//              Process packet
//                Controller.getInstance().workWithPacket(packet.array());

                stopConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void sendResponse(byte[] message) {
            synchronized (out) {
                try {
                    out.write(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void stopConnection() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true)
                new ClientHandler(serverSocket.accept()).start();
//            TODO not while true, but appropriate amount of handlers.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StoreServerTCP server = new StoreServerTCP();
        server.start(6666);
    }
}
