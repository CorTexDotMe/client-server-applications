package com.ukma.nechyporchuk.network.implementation.tcp;

import com.ukma.nechyporchuk.core.Controller;
import com.ukma.nechyporchuk.core.Message;
import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.utils.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class StoreServerTCP {
    private ServerSocket serverSocket;

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
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
//                byte bMagic;
//                do {
//
//                    bMagic = in.readByte();             // Trying to find magic byte in order to start reading packet
//                } while (bMagic != Constants.bMagic);
//
//                byte bSrc = in.readByte();
//                long bPktId = in.readLong();
//                int wLen = in.readInt();
//                short wCrc16_first = in.readShort();
//
//                ByteBuffer packet = ByteBuffer.wrap(new byte[
//                        Constants.BYTES_AMOUNT_FOR_FIRST_CHECKSUM +
//                        Constants.BYTES_AMOUNT_OF_CRC +
//                        wLen +
//                        Constants.BYTES_AMOUNT_OF_CRC
//                        ]);
//                packet.put(bMagic).put(bSrc).putLong(bPktId).putInt(wLen).putShort(wCrc16_first);
//                packet.put(in.readNBytes(wLen));
//                short wCrc16_second = in.readShort();
//                packet.putShort(wCrc16_second);

//              Process packet
//              TODO 3 bEnd bytes to stop connection. If yes then stop. PLUS RECONNECT. PLUS NOT WHILE TRUE
                while (true) {
                    byte inputByte = in.readByte();
                    switch (inputByte) {
                        case Constants.bMagic:
                            Controller.getInstance().workWithTCPPacket(in, out).get();
                            break;
                        case Constants.bEnd:
//                            out.writeUTF(".");
                            out.write(new Packet((byte) 0, 0L, new Message(0, 0, "good bye".getBytes())).getPacket());
                            stopConnection();
                            return;
                    }
                }

//                while ((inputLine = in.read()) != null) {
//                if (Arrays.equals(".".getBytes(), inputLine)) {
//                    out.write(new Packet((byte) 0, 0L, new Message(0, 0, "good bye".getBytes())).getPacket());
//                        break;
//                } else

//                }
//                while (true) {
//                }


            } catch (IOException e) {
                e.printStackTrace();
                stopConnection();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
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
//          TODO not while true, but appropriate amount of handlers.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StoreServerTCP server = new StoreServerTCP();
        server.start(6666);
    }
}
