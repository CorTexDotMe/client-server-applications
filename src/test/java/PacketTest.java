import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacketTest {

    private final Packet packetSent =
            new Packet((byte) 1, 15, new Message(100, 10, "Ukulele".getBytes()));;
    private final Packet packetReceived = new Packet(this.packetSent.getPacket());

    @Test
    void getPacket() {
        assertEquals(this.packetSent.getPacket(), this.packetReceived.getPacket());
    }
    @Test
    void getBSrc() {
        assertEquals(this.packetSent.getBSrc(), this.packetReceived.getBSrc());
    }

    @Test
    void getBPktId() {
        assertEquals(this.packetSent.getBPktId(), this.packetReceived.getBPktId());
    }

    @Test
    void getWLen() {
        assertEquals(this.packetSent.getWLen(), this.packetReceived.getWLen());
    }

    @Test
    void getWCrc16_first() {
        assertEquals(this.packetSent.getWCrc16_first(), this.packetReceived.getWCrc16_first());
    }

    @Test
    void getBMsgCType() {
        assertEquals(this.packetSent.getBMsg().getcType(), this.packetReceived.getBMsg().getcType());
    }

    @Test
    void getBMsgBUsrId() {
        assertEquals(this.packetSent.getBMsg().getbUserId(), this.packetReceived.getBMsg().getbUserId());
    }

    @Test
    void getBMsgMessage() {
        assertArrayEquals(this.packetSent.getBMsg().getMessage(), this.packetReceived.getBMsg().getMessage());
    }

    @Test
    void getWCrc16_second() {
        assertEquals(this.packetSent.getWCrc16_second(), this.packetReceived.getWCrc16_second());
    }
}