package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.core.entities.Message;
import com.ukma.nechyporchuk.core.entities.Packet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class PacketTest {

    private static Packet packetSent;
    private static Packet packetReceived;

    @BeforeAll
    static void initialize() {
        packetSent = new Packet((byte) 1, 15, new Message(100, 10, "Ukulele".getBytes()));

        packetReceived = new Packet(packetSent.getBytes());
    }

    @Test
    void getPacket() {
        assertEquals(packetSent.getBytes(), packetReceived.getBytes());
    }

    @Test
    void getBSrc() {
        assertEquals(packetSent.getBSrc(), packetReceived.getBSrc());
    }

    @Test
    void getBPktId() {
        assertEquals(packetSent.getBPktId(), packetReceived.getBPktId());
    }

    @Test
    void getWLen() {
        assertEquals(packetSent.getWLen(), packetReceived.getWLen());
    }

    @Test
    void getWCrc16_first() {
        assertEquals(packetSent.getWCrc16_first(), packetReceived.getWCrc16_first());
    }

    @Test
    void getBMsgCType() {
        assertEquals(packetSent.getBMsg().getCType(), packetReceived.getBMsg().getCType());
    }

    @Test
    void getBMsgBUsrId() {
        assertEquals(packetSent.getBMsg().getBUserId(), packetReceived.getBMsg().getBUserId());
    }

    @Test
    void getBMsgMessage() {
        assertArrayEquals(packetSent.getBMsg().getMessage(), packetReceived.getBMsg().getMessage());
    }

    @Test
    void getWCrc16_second() {
        assertEquals(packetSent.getWCrc16_second(), packetReceived.getWCrc16_second());
    }

    @Test
    void wrongSRC() {
        assertThrows(IllegalArgumentException.class, () -> {
            byte[] wrongSrcPacket = packetSent.getBytes();
            wrongSrcPacket[1] = (byte) ((int) packetSent.getBSrc() + 1);

            packetReceived = new Packet(wrongSrcPacket);
        });
    }

    @Test
    void wrongPktId() {
        assertThrows(IllegalArgumentException.class, () -> {
            ByteBuffer wrongPktIdPacket = ByteBuffer.wrap(packetSent.getBytes());
            wrongPktIdPacket.putLong(2, (packetSent.getBPktId() + 1));

            packetReceived = new Packet(wrongPktIdPacket.array());
        });
    }

    @Test
    void wrongLen() {
        assertThrows(IllegalArgumentException.class, () -> {
            ByteBuffer wrongLenPacket = ByteBuffer.wrap(packetSent.getBytes());
            wrongLenPacket.putInt(10, (packetSent.getWLen() + 1));

            packetReceived = new Packet(wrongLenPacket.array());
        });
    }

    @Test
    void wrongMsg() {
        assertThrows(IllegalArgumentException.class, () -> {
            ByteBuffer wrongMsgPacket = ByteBuffer.wrap(packetSent.getBytes());
            wrongMsgPacket.put(17, (byte) ((int) wrongMsgPacket.get(17) + 1));

            packetReceived = new Packet(wrongMsgPacket.array());
        });
    }
}