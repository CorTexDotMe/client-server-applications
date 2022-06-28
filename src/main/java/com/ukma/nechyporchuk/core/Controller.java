package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.network.implementation.Receiver;
import com.ukma.nechyporchuk.network.implementation.Sender;
import com.ukma.nechyporchuk.security.Decryptor;
import com.ukma.nechyporchuk.security.Encryptor;

import javax.lang.model.element.Element;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
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
//    private static Controller INSTANCE;
//    private final ThreadPoolExecutor threadPool;
//    private final Sender sender;
//
//    private Controller() {
//        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
//        sender = new Sender();
//    }
//
//    public static Controller getInstance() {
//        if (INSTANCE == null)
//            INSTANCE = new Controller();
//
//        return INSTANCE;
//    }
//
//    public void workWithPacket(byte[] packet) {
//        threadPool.execute(() -> processPacket(packet));
//    }
//
//    public void shutdown() {
//        threadPool.shutdown();
//    }
//
//    /**
//     * To process packet it needs to be decrypted and processed.
//     * Then response is created and sent via Sender
//     *
//     * @param packet byte array with all information
//     */
//    private void processPacket(byte[] packet) {
//        // Decryption
//        Packet decryptedPacket = new Packet(packet);
//
//        // Processing
//        Processor processor = new Processor();
//        Message response = processor.process(decryptedPacket.getBMsg());
//
//        // Encryption
//        Packet responsePacket = new Packet(
//                decryptedPacket.getBSrc(),
//                decryptedPacket.getBPktId() + 1,
//                response
//        );
//
//        // Sending
//        synchronized (sender) {
//            sender.sendMessage(responsePacket.getPacket(), null);
//        }
//    }
//
//
//    public static void main(String[] args) {
//        Controller controller = Controller.getInstance();
//        Receiver receiver = new Receiver();
//
//        for (int i = 0; i < 100000; i++) {
//            receiver.receiveMessage();
//        }
//        controller.shutdown();
//    }

    public static void processPacket(DataInputStream in, InetAddress address) {
        Receiver receiver = new Receiver(in);
        receiver.receiveMessage();
        Decryptor.decrypt();
        Processor.process();
        Encryptor.encrypt();
        Sender sender = new Sender();
        sender.sendMessage(address);
    }

    private void send1000Messages() {
        for (int i = 0; i < 100000; i++) {
            Controller.processPacket(null, null);
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        Controller controller = new Controller();
        for (int i = 0; i < 1; i++)
//            threadPool.execute(controller::send1000Messages);
            controller.send1000Messages();
    }


}
