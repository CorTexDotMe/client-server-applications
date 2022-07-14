package com.ukma.nechyporchuk.network.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.nechyporchuk.core.entities.Message;
import com.ukma.nechyporchuk.core.entities.Packet;
import com.ukma.nechyporchuk.core.utils.Constants;

import java.util.HashMap;
import java.util.Random;

public interface Receiver {
    void receiveMessage();

    byte[] poll();

    static byte[] getRandomPacket() {
        try {
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

            ObjectMapper OBJECT_MAPPER = new ObjectMapper();
            Message bMsg = new Message(cType, bUserId, OBJECT_MAPPER.writeValueAsBytes(new HashMap<String, String>()));

            Packet message = new Packet(bSrc, bPktId, bMsg);
            return message.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
