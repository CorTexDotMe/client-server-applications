package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.network.fake.FakeReceiver;
import com.ukma.nechyporchuk.network.fake.FakeSender;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Controller that has ThreadPool to process different packets using multiple threads
 * Use cached thread pool, so it can create a lot of threads.
 * Saved as Singleton.
 * <p>
 * Use shutdown() to stop all threads.
 */
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
        threadPool.execute(() -> processPacket(packet));
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    /**
     * To process packet it needs to be decrypted and processed.
     * Then response is created and sent via Sender
     *
     * @param packet byte array with all information
     */
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
