package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.core.CommandAnalyser;
import com.ukma.nechyporchuk.core.Message;
import com.ukma.nechyporchuk.network.implementation.Receiver;
import com.ukma.nechyporchuk.security.Decryptor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Processor that can create response based on different types of commands.
 * Types are determined in CommandAnalyser
 */
public class Processor {
    public final static BlockingQueue<ProcessorMessage> processedMessages = new LinkedBlockingQueue<>();

    public static void process() {
        new Thread(() -> {
            try {
                ProcessorMessage message = null;
                synchronized (Decryptor.decryptedMessages) {
                    while (message == null)
                        message = Decryptor.decryptedMessages.poll(500L, TimeUnit.MILLISECONDS);
                }

                byte[] response;

                switch (message.getMessage().getCType()) {
                    case CommandAnalyser.ITEM_GET:
                        response = "OK_GET".getBytes();
                        break;
                    case CommandAnalyser.ITEM_REMOVE:
                        response = "OK_REMOVE".getBytes();
                        break;
                    case CommandAnalyser.ITEM_ADD:
                        response = "OK_ADD".getBytes();
                        break;
                    case CommandAnalyser.ITEM_CREATE:
                        response = "OK_CREATE_ITEM".getBytes();
                        break;
                    case CommandAnalyser.GROUP_CREATE:
                        response = "OK_CREATE_GROUP".getBytes();
                        break;
                    case CommandAnalyser.ITEM_SET_PRICE:
                        response = "OK_SET_PRICE".getBytes();
                        break;
                    default:
                        response = "Wrong command".getBytes();
                }

                processedMessages.put(
                        new ProcessorMessage(
                                message.getbSrc(),
                                message.getbPktId() + 1,
                                new Message(
                                        message.getMessage().getCType(),
                                        message.getMessage().getBUserId(),
                                        response
                                )
                        )
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
