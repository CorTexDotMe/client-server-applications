package com.ukma.nechyporchuk.network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class StoreServerTCP {
    private ServerSocket serverSocket;

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String input = in.readLine();
                out.println(input);

                stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void stop() {
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
                new Thread(new ClientHandler(serverSocket.accept()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
