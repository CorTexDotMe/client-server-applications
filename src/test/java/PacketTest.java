import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacketTest {

    private static Packet packetSent;
    private static Packet packetReceived;
    
    @BeforeAll
    static void initialize() {
        packetSent = new Packet((byte) 1, 15, new Message(100, 10, "Ukulele".getBytes()));

        packetReceived = new Packet(packetSent.getPacket());
    }

    @Test
    void getPacket() {
        assertEquals(packetSent.getPacket(), packetReceived.getPacket());
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
}