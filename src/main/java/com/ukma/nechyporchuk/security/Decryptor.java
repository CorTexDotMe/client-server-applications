package com.ukma.nechyporchuk.security;

import com.ukma.nechyporchuk.core.Message;
import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.core.ProcessorMessage;
import com.ukma.nechyporchuk.network.implementation.Receiver;
import com.ukma.nechyporchuk.utils.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Decryptor {
    public final static BlockingQueue<ProcessorMessage> decryptedMessages = new LinkedBlockingQueue<>();

    public static void decrypt() {
        new Thread(() -> {
            try {
                byte[] packet = null;
                synchronized (Receiver.receivedPackets) {
                    while (packet == null)
                        packet = Receiver.receivedPackets.poll(500L, TimeUnit.MILLISECONDS);
                }

                Packet decrypted = new Packet(packet);

                decryptedMessages.put(
                        new ProcessorMessage(
                                decrypted.getBSrc(),
                                decrypted.getBPktId(),
                                decrypted.getBMsg()
                        )
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
