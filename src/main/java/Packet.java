import java.nio.ByteBuffer;

public class Packet {
    public static final byte bMagic = 0x13;
    private byte bSrc;
    private long bPktId;
    private int wLen;
    private short wCrc16;
    private Message bMsg;
    private short wCrc16_second;

    public Packet(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        if (buffer.get() != bMagic)
            throw new IllegalArgumentException("No start byte in byte array");
        bSrc = buffer.get();
        bPktId = buffer.getLong();
        wLen = buffer.getInt();
        wCrc16 = buffer.getShort();
        bMsg = new Message(buffer, wLen);
        wCrc16_second = buffer.getShort();
    }
}
