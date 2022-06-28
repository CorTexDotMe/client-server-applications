package com.ukma.nechyporchuk.network.implementation;

import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.core.Processor;
import com.ukma.nechyporchuk.core.ProcessorMessage;
import com.ukma.nechyporchuk.security.Decryptor;
import com.ukma.nechyporchuk.security.Encryptor;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Print packet message in console instead of sending packet.
 */
public class Sender implements com.ukma.nechyporchuk.network.interfaces.Sender {

    private static final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static void workWithPacket(byte[] packet) {
        synchronized (threadPool) {
            threadPool.execute(() -> {
                Packet response = new Packet(packet);

                System.out.println(
                        response.getBPktId() + " (bPktId). " +
                        new String(response.getBMsg().getMessage(), StandardCharsets.UTF_8)
                );
            });
        }
    }

    @Override
    public void sendMessage(InetAddress target) {
        new Thread(() -> {
            try {
                byte[] packet = null;
                synchronized (Encryptor.responsePackets) {
                    while (packet == null)
                        packet = Encryptor.responsePackets.poll(500L, TimeUnit.MILLISECONDS);
                }

                workWithPacket(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
