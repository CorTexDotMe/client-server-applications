package com.ukma.nechyporchuk.network.interfaces;

import com.ukma.nechyporchuk.entities.Message;
import com.ukma.nechyporchuk.entities.Packet;
import com.ukma.nechyporchuk.utils.Constants;

import java.util.Random;

public interface Receiver {
    void receiveMessage();

    byte[] poll();

    static byte[] getRandomPacket() {
        Random random = new Random();

        byte bSrc = (byte) random.nextInt(256);
        long bPktId = Constants.bPktIdForTesting;
        Constants.bPktIdForTesting += 2;

        int cType = Integer.MAX_VALUE;
        /*
        switch (random.nextInt(6)) {
            case 0 -> cType = CommandAnalyser.ITEM_GET;
            case 1 -> cType = CommandAnalyser.ITEM_REMOVE;
            case 2 -> cType = CommandAnalyser.ITEM_ADD;
            case 3 -> cType = CommandAnalyser.ITEM_CREATE;
            case 4 -> cType = CommandAnalyser.GROUP_CREATE;
            default -> cType = CommandAnalyser.ITEM_SET_PRICE;
        }
         */

        int bUserId = random.nextInt();
        byte[] messageBytes = new byte[0];
        Message bMsg = new Message(cType, bUserId, messageBytes);
        Packet message = new Packet(bSrc, bPktId, bMsg);
        return message.getBytes();
    }
}
