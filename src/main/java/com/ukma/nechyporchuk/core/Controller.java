package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.network.fake.FakeReceiver;
import com.ukma.nechyporchuk.network.fake.FakeSender;
import com.ukma.nechyporchuk.security.PacketCipher;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Controller {
    private static Controller INSTANCE;
    private final ThreadPoolExecutor threadPool;
    private final FakeSender sender;

    private Controller() {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        sender = new FakeSender();
    }

    public static Controller getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Controller();

        return INSTANCE;
    }

    public void workWithPacket(byte[] packet) {
        threadPool.execute(() -> {
            processPacket(packet);
        });
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    private void processPacket(byte[] packet) {
        // Decryption
        Packet decryptedPacket = new Packet(packet);

        // Processing
        Processor processor = new Processor();
        Message response = processor.process(decryptedPacket.getBMsg());

        // Encryption
        Packet responsePacket = new Packet(
                decryptedPacket.getBSrc(),
                decryptedPacket.getBPktId() + 1,
                response
        );

        // Sending
        synchronized (sender) {
            sender.sendMessage(responsePacket.getPacket(), null);
        }
    }


    public static void main(String[] args) {
        Controller controller = Controller.getInstance();
        FakeReceiver receiver = new FakeReceiver();

        for (int i = 0; i < 100000; i++) {
            receiver.receiveMessage();
        }
        controller.shutdown();
    }
}
