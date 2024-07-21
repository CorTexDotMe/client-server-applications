package com.ukma.stockmanager.network.implementation.tcp;

import com.ukma.stockmanager.core.utils.Constants;
import com.ukma.stockmanager.network.interfaces.Receiver;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TCPTest {

    private static StoreServerTCP server;

    @BeforeAll
    static void setUp() {
        new Thread(() -> {
            server = new StoreServerTCP();
            server.start(Constants.TCP_PORT);
        }).start();
    }

    @Test
    @Order(1)
    void test1() {
        try {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    void test2() {
        try {
            StoreClientTCP client1 = new StoreClientTCP();
            client1.startConnection("127.0.0.1", Constants.TCP_PORT);
            client1.sendMessage(Receiver.getRandomPacket());

            StoreClientTCP client2 = new StoreClientTCP();
            client2.startConnection("127.0.0.1", Constants.TCP_PORT);
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

    @Test
    @Order(3)
    void testMultipleClients1() {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        TCPTest tcp = new TCPTest();

        for (int i = 0; i < 100; i++)
            threadPool.execute(tcp::test1);

        try {
            threadPool.awaitTermination(2L, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    @Order(4)
    void testMultipleClients2() {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        TCPTest tcp = new TCPTest();

        for (int i = 0; i < 100; i++)
            threadPool.execute(tcp::test2);

        try {
            threadPool.awaitTermination(2L, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    @Order(5)
    void lostConnectionTest() {
        try {
            StoreClientTCP client = new StoreClientTCP();

            client.startConnection("127.0.0.1", Constants.TCP_PORT);

            client.stopConnection(); // Test lost connection
            String response1 = client.sendAndReceiveMessage(Receiver.getRandomPacket());
            System.out.println(response1);

            client.stopConnection(); // Test lost connection
            String response2 = client.sendAndReceiveMessage(Receiver.getRandomPacket());
            System.out.println(response2);

            client.sendEndMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void tearDown() {
        server.stopServer();
    }
}