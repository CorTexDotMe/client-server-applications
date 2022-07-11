package com.ukma.nechyporchuk.processor;

import com.ukma.nechyporchuk.core.entities.Message;
import com.ukma.nechyporchuk.core.entities.Packet;
import com.ukma.nechyporchuk.network.implementation.tcp.TCPSender;
import com.ukma.nechyporchuk.network.implementation.udp.UDPSender;
import com.ukma.nechyporchuk.network.interfaces.Receiver;
import com.ukma.nechyporchuk.network.interfaces.Sender;

import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    public Future<?> workWithTCPPacket(Receiver receiver, DataOutputStream out) {
        return threadPool.submit(() -> processPacket(
                receiver,
                new TCPSender(out)
        ));
    }

    public void workWithUDPPacket(Receiver receiver, DatagramSocket datagramSocket, DatagramPacket datagramPacket) {
        threadPool.execute(() -> processPacket(
                receiver,
                new UDPSender(datagramSocket, datagramPacket)
        ));
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    /**
     * To process packet it needs to be decrypted and processed.
     * Then response is created and sent via Sender
     */
    private void processPacket(Receiver receiver, Sender sender) {

        //Receive
        byte[] packet = receiver.poll();

        if (packet == null)
            return;

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
        sender.sendMessage(responsePacket.getBytes());
    }


    public static void main(String[] args) {
        Controller controller = Controller.getInstance();
//        Receiver receiver = new Receiver();

//        for (int i = 0; i < 100000; i++) {
//            receiver.receiveMessage();
//        }
        controller.shutdown();
    }
}
