package com.ukma.nechyporchuk.network.implementation;

import com.ukma.nechyporchuk.core.CommandAnalyser;
import com.ukma.nechyporchuk.core.Message;
import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.utils.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Create packet out of thin air.
 * Random class is used
 *
 * @author Danylo Nechyporchuk
 */
public class Receiver implements com.ukma.nechyporchuk.network.interfaces.Receiver {

    private DataInputStream in;

    public Receiver(DataInputStream in) {
        this.in = in;
    }

    @Override
    public byte[] receiveMessage() {
        try {
            byte bSrc = in.readByte();
            long bPktId = in.readLong();
            int wLen = in.readInt();
            short wCrc16_first = in.readShort();
            ByteBuffer packet = ByteBuffer.wrap(new byte[
                    Constants.BYTES_AMOUNT_FOR_FIRST_CHECKSUM +
                    Constants.BYTES_AMOUNT_OF_CRC +
                    wLen +
                    Constants.BYTES_AMOUNT_OF_CRC
                    ]);
            packet.put(Constants.bMagic).put(bSrc).putLong(bPktId).putInt(wLen).putShort(wCrc16_first);
            packet.put(in.readNBytes(wLen));
            short wCrc16_second = in.readShort();
            packet.putShort(wCrc16_second);


            return packet.array();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long bPktId = 0;

    public static byte[] getRandomPacket() {
        Random random = new Random();

        byte bSrc = (byte) random.nextInt(256);
        long bPktId = Receiver.bPktId;
        Receiver.bPktId += 2;

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
        return message.getPacket();
    }
}
