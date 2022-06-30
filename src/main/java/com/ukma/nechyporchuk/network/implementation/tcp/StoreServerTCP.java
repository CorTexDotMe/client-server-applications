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
    private boolean running = true;

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

//              Process packet
                while (true) {
                    byte inputByte = in.readByte();
                    switch (inputByte) {
                        case Constants.bMagic:
                            process();
                            break;
                        case Constants.bEnd:
                            int bEndBytesAmount = 1;

                            for (int i = 1; i < Constants.bEndMessage.length; i++) {
                                inputByte = in.readByte();
                                if (inputByte == Constants.bMagic) {
                                    process();
                                    break;
                                }
                                if (inputByte != Constants.bEnd)
                                    break;

                                bEndBytesAmount++;
                            }
                            if (bEndBytesAmount == Constants.bEndMessage.length) {
                                sendEndResponse();
                                stopConnection();
                                return;
                            }
                    }
                }

            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
                stopConnection();
            }
        }

        private void process() throws ExecutionException, InterruptedException {
            TCPReceiver receiver = new TCPReceiver(in);
            receiver.receiveMessage();
            Controller.getInstance().workWithTCPPacket(receiver, out).get();
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

        private void sendEndResponse() throws IOException {
            out.write(new Packet((byte) 0, 0L, new Message(0, 0, "good bye".getBytes())).getPacket());
        }
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (running)
                new ClientHandler(serverSocket.accept()).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
    }

    public static void main(String[] args) {
        StoreServerTCP server = new StoreServerTCP();
        server.start(Constants.TCP_PORT);
    }
}
