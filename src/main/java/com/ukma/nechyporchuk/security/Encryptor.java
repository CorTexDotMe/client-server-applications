package com.ukma.nechyporchuk.security;

import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.core.Processor;
import com.ukma.nechyporchuk.core.ProcessorMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Encryptor {
    public final static BlockingQueue<byte[]> responsePackets = new LinkedBlockingQueue<>();

    public static void encrypt() {
        new Thread(() -> {
            try {
                ProcessorMessage message = null;
                synchronized (Processor.processedMessages) {
                    while (message == null)
                        message = Processor.processedMessages.poll(500L, TimeUnit.MILLISECONDS);
                }

                responsePackets.put(
                        new Packet(message).getPacket()
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
