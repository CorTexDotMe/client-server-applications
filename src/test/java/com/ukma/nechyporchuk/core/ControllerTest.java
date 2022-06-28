package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.network.implementation.Receiver;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

//    private void send1000Messages() {
//        Receiver receiver = new Receiver();
//
//        for (int i = 0; i < 1000; i++)
//            receiver.receiveMessage();
//    }
//
//    @Test
//    void workWithPacket() {
//        assertDoesNotThrow(() -> {
//            ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
//
//            for (int i = 0; i < 100; i++)
//                threadPool.execute(this::send1000Messages);
//        });
//        Controller.getInstance().shutdown();
//    }
}