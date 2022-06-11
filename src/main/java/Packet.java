import helpers.CRC16;

import java.nio.ByteBuffer;

public class Packet {
    public static final int BYTES_AMOUNT_FOR_FIRST_CHECKSUM = 14;
    public static final int BYTES_OFFSET_FOR_FIRST_CHECKSUM = 0;
    public static final int BYTES_OFFSET_FOR_SECOND_CHECKSUM = 16;
    public static final byte bMagic = 0x13;

    private byte bSrc;
    private long bPktId;
    private int wLen;
    private short wCrc16_first;
    private Message bMsg;
    private short wCrc16_second;

    public Packet(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        if (buffer.get() != bMagic)
            throw new IllegalArgumentException("No start byte in byte array");

        bSrc = buffer.get();
        bPktId = buffer.getLong();
        wLen = buffer.getInt();
        wCrc16_first = buffer.getShort();

        if (wrongChecksum(wCrc16_first, buffer, BYTES_OFFSET_FOR_FIRST_CHECKSUM, BYTES_AMOUNT_FOR_FIRST_CHECKSUM))
            throw new IllegalArgumentException("Wrong first checksum");

        bMsg = new Message(buffer, wLen);
        wCrc16_second = buffer.getShort();

        if (wrongChecksum(wCrc16_second, buffer, BYTES_OFFSET_FOR_SECOND_CHECKSUM, wLen))
            throw new IllegalArgumentException("Wrong second checksum");
    }

    private boolean wrongChecksum(short expectedChecksum, ByteBuffer buffer,
                                  int packetOffset, int packetToCheckLength) {
        byte[] bytesForChecksum = new byte[packetToCheckLength];
        buffer.get(bytesForChecksum, packetOffset, packetToCheckLength);
        return expectedChecksum != CRC16.getCRC16(bytesForChecksum);
    }
}
