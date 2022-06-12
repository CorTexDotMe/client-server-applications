import helpers.CRC16;
import helpers.PacketCipher;

import java.nio.ByteBuffer;

public class Packet {
    private static final int BYTES_AMOUNT_FOR_FIRST_CHECKSUM = 14;
    private static final int PACKET_LENGTH_WITHOUT_MESSAGE = 18;
    private static final byte bMagic = 0x13;

    private byte bSrc;
    private long bPktId;
    private int wLen;
    private short wCrc16_first;
    private Message bMsg;
    private short wCrc16_second;
    private byte[] packet;

    private static final PacketCipher cipher = new PacketCipher();

    public Packet(byte bSrc, long bPktId, Message bMsg) {
        this.bMsg = bMsg;
        ByteBuffer messageBuffer = ByteBuffer.allocate(Integer.BYTES * 2 + bMsg.getMessage().length);
        messageBuffer.putInt(bMsg.getcType());
        messageBuffer.putInt(bMsg.getbUserId());
        messageBuffer.put(bMsg.getMessage());
        byte[] encryptedMessage = cipher.encryptData(messageBuffer.array());

        this.wLen = encryptedMessage.length;
        this.packet = new byte[PACKET_LENGTH_WITHOUT_MESSAGE + wLen];
        ByteBuffer buffer = ByteBuffer.wrap(this.packet);
        buffer.mark();

        buffer.put(bMagic);
        this.bSrc = bSrc;
        buffer.put(bSrc);
        this.bPktId = bPktId;
        buffer.putLong(bPktId);
        buffer.putInt(wLen);

        byte[] bytesForFirstChecksum = new byte[BYTES_AMOUNT_FOR_FIRST_CHECKSUM];
        buffer.reset();
        buffer.get(bytesForFirstChecksum);
        this.wCrc16_first = CRC16.getCRC16(bytesForFirstChecksum);
        buffer.putShort(this.wCrc16_first);

        buffer.put(encryptedMessage);

        this.wCrc16_second = CRC16.getCRC16(encryptedMessage);
        buffer.putShort(this.wCrc16_second);
    }

    public Packet(byte[] bytes) {
        this.packet = bytes;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.mark();

        if (buffer.get() != bMagic)
            throw new IllegalArgumentException("No start byte in byte array");

        this.bSrc = buffer.get();
        this.bPktId = buffer.getLong();
        this.wLen = buffer.getInt();

        byte[] bytesForFirstChecksum = new byte[BYTES_AMOUNT_FOR_FIRST_CHECKSUM];
        buffer.reset();
        buffer.get(bytesForFirstChecksum);
        this.wCrc16_first = buffer.getShort();
        if (wrongChecksum(this.wCrc16_first, bytesForFirstChecksum))
            throw new IllegalArgumentException("Wrong first checksum");

        byte[] encryptedMessage = new byte[wLen];
        buffer.get(encryptedMessage);
        byte[] decryptedMessage = cipher.decryptData(encryptedMessage);
        this.bMsg = new Message(ByteBuffer.wrap(decryptedMessage));

        this.wCrc16_second = buffer.getShort();
        if (wrongChecksum(this.wCrc16_second, encryptedMessage))
            throw new IllegalArgumentException("Wrong second checksum");
    }

    public byte[] getPacket() {
        return packet;
    }

    public byte getBSrc() {
        return bSrc;
    }

    public long getBPktId() {
        return bPktId;
    }

    public int getWLen() {
        return wLen;
    }

    public short getWCrc16_first() {
        return wCrc16_first;
    }

    public Message getBMsg() {
        return bMsg;
    }

    public short getWCrc16_second() {
        return wCrc16_second;
    }

    private boolean wrongChecksum(short expectedChecksum, byte[] packetToCheck) {
        return expectedChecksum != CRC16.getCRC16(packetToCheck);
    }
}
