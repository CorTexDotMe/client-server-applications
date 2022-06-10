import java.nio.ByteBuffer;

public class PacketToSend {
    private final int packetLength = 18;
    private byte[] packet;

    public PacketToSend(byte bSrc, long bPktId, int wLen, Message bMsg) {
        packet = new byte[packetLength + wLen];
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        buffer.put(Packet.bMagic);
        buffer.put(bSrc);
        buffer.putLong(bPktId);
        buffer.putInt(wLen);

        //buffer.putShort(wCrc16);

        buffer.putInt(bMsg.getcType());
        buffer.putInt(bMsg.getbUserId());
        buffer.put(bMsg.getMessage());

        //buffer.putShort(wCrc16_second);
    }

    public byte[] getPacket() {
        return packet;
    }
}
