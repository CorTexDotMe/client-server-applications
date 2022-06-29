package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.network.implementation.Receiver;
import com.ukma.nechyporchuk.network.implementation.Sender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    private Controller() {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public static Controller getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Controller();

        return INSTANCE;
    }

    public void workWithPacket(DataInputStream in, DataOutputStream out) {
        threadPool.execute(() -> processPacket(in, out));
//        return threadPool.getThreadFactory().newThread(() -> processPacket(in, out));
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    /**
     * To process packet it needs to be decrypted and processed.
     * Then response is created and sent via Sender
     */
    private void processPacket(DataInputStream in, DataOutputStream out) {

        //Receive
        Receiver receiver = new Receiver(in);
        byte[] packet = receiver.receiveMessage();

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
        Sender sender = new Sender(out);
        sender.sendMessage(responsePacket.getPacket(), null);

    }


    public static void main(String[] args) {
        Controller controller = Controller.getInstance();
//        Receiver receiver = new Receiver();

        for (int i = 0; i < 100000; i++) {
//            receiver.receiveMessage();
        }
        controller.shutdown();
    }
}
