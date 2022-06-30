package com.ukma.nechyporchuk.network.implementation.udp;

import com.ukma.nechyporchuk.core.Controller;
import com.ukma.nechyporchuk.network.interfaces.Receiver;
import com.ukma.nechyporchuk.utils.Constants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UDPTest {

    private static StoreServerUDP server;

    @BeforeAll
    static void setUp() {
        server = new StoreServerUDP();
        server.start();
    }

    @Test
    void test1() {
        StoreClientUDP client = new StoreClientUDP();
        client.sendMessage(Receiver.getRandomPacket(), Constants.UDP_PORT);
        client.sendMessage(Receiver.getRandomPacket(), Constants.UDP_PORT);

        String response1 = client.receiveMessage(Constants.UDP_PORT);
        String response2 = client.receiveMessage(Constants.UDP_PORT);

        System.out.println(response1);
        System.out.println(response2);
        assertTrue(response1.contains("OK") || response2.contains("OK"));
    }

    @Test
    void test2() {
        StoreClientUDP client1 = new StoreClientUDP();
        StoreClientUDP client2 = new StoreClientUDP();

        client1.sendMessage(Receiver.getRandomPacket(), Constants.UDP_PORT);
        client2.sendMessage(Receiver.getRandomPacket(), Constants.UDP_PORT);

        String response1 = client1.receiveMessage(Constants.UDP_PORT);
        String response2 = client2.receiveMessage(Constants.UDP_PORT);
        System.out.println(response1);
        System.out.println(response2);
        assertTrue(response1.contains("OK") || response2.contains("OK"));
    }

    @Test
    void testMultipleClients1() {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        UDPTest test = new UDPTest();


        for (int i = 0; i < 100; i++)
            threadPool.execute(test::test1);
//            new Thread(() -> {
//                test.test1();
//            }).start();

//        threadPool.shutdown();
        try {
            threadPool.awaitTermination(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    @Test
    void testMultipleClients2() {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        UDPTest test = new UDPTest();


        for (int i = 0; i < 100; i++)
            threadPool.execute(test::test2);
//            new Thread(() -> {
//                test.test2();
//            }).start();

//        threadPool.shutdown();
        try {
            threadPool.awaitTermination(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    @AfterAll
    static void tearDown() {
        server.stopServer();
    }
}