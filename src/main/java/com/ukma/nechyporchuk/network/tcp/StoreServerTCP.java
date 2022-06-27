package com.ukma.nechyporchuk.network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class StoreServerTCP {
    private ServerSocket serverSocket;

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String input = in.readLine();
                do {
                    System.out.println(input);
                    out.println("Message has been received");
                    input = in.readLine();
                    if (input.equals(".")) {
                        out.println("bye");
                        break;
                    }
                } while (input != null);

                stopConnection();

            } catch (IOException e) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StoreServerTCP server = new StoreServerTCP();
        server.start(6666);
    }
}
