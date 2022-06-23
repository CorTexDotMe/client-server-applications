package com.ukma.nechyporchuk.network.fake;

import com.ukma.nechyporchuk.core.CommandAnalyser;
import com.ukma.nechyporchuk.core.Controller;
import com.ukma.nechyporchuk.core.Message;
import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.network.interfaces.Receiver;

import java.util.Random;

/**
 * Create packet out of thin air.
 * Random class is used
 *
 * @author Danylo Nechyporchuk
 */
public class FakeReceiver implements Receiver {
    private long bPktId = 0;

    @Override
    public void receiveMessage() {
        Random random = new Random();


        byte bSrc = (byte) random.nextInt(256);
        long bPktId = this.bPktId += 2;

        int cType;
        switch (random.nextInt(6)) {
            case 0 -> cType = CommandAnalyser.ITEM_GET;
            case 1 -> cType = CommandAnalyser.ITEM_REMOVE;
            case 2 -> cType = CommandAnalyser.ITEM_ADD;
            case 3 -> cType = CommandAnalyser.ITEM_CREATE;
            case 4 -> cType = CommandAnalyser.GROUP_CREATE;
            default -> cType = CommandAnalyser.ITEM_SET_PRICE;
        }

        int bUserId = random.nextInt();
        byte[] messageBytes = new byte[0];
        Message bMsg = new Message(cType, bUserId, messageBytes);
        Packet message = new Packet(bSrc, bPktId, bMsg);

        Controller.getInstance().workWithPacket(message.getPacket());
    }
}
