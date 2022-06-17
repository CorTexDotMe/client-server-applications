package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.helpers.CRC16;
import com.ukma.nechyporchuk.security.PacketCipher;

import java.nio.ByteBuffer;

/**
 * Уявимо, що ви розробляєте клієнт серверне застосування, що має обмінюватися повідомленнями по мережі.
 * Ваші дані містять комерційну таємницю і не можуть передаватися в відкритому вигляді.
 * Тому ви маєте розробити протокол обміну повідомленнями. Таким чином структура нашого пакету:
 * <p>
 * Offset	Length	Mnemonic	Notes
 * 00	    1	    bMagic	    Байт, що вказує на початок пакету - значення 13h (h - значить hex)
 * 01	    1	    bSrc	    Унікальний номер клієнтського застосування
 * 02	    8	    bPktId	    Номер повідомлення. Номер постійно збільшується. В форматі big-endian
 * 10	    4	    wLen	    Довжина пакету даних big-endian
 * 14	    2	    wCrc16	    CRC16 байтів (00-13) big-endian
 * 16	    wLen	bMsq	    Message - корисне повідомлення
 * 16+wLen	2	    wCrc16	    CRC16 байтів (16 до 16+wLen-1) big-endian
 *
 * @author Danylo Nechyporchuk
 */
public class Packet {
    public static final int PACKET_LENGTH_WITHOUT_MESSAGE = 18;
    private static final int BYTES_AMOUNT_FOR_FIRST_CHECKSUM = 14;
    public static final byte bMagic = 0x13;

    private byte bSrc;
    private long bPktId;
    private int wLen;
    private short wCrc16_first;
    private Message bMsg;
    private short wCrc16_second;
    private byte[] packet;

    private static final PacketCipher cipher = new PacketCipher();

    /**
     * Constructor is used to create packet before sending.
     * Take necessary data as parameters.
     * Result is stored in field [packet].
     * <p>
     * Message is encrypted with AES-GCM algorithm.
     *
     * @param bSrc   unique client id
     * @param bPktId packet id. Will be incremented
     * @param bMsg   message. Will be encrypted
     */
    public Packet(byte bSrc, long bPktId, Message bMsg) {
        //Encrypt message to find out its lengths
        ByteBuffer messageBuffer = ByteBuffer.allocate(Integer.BYTES * 2 + bMsg.getMessage().length);
        messageBuffer.putInt(bMsg.getCType());
        messageBuffer.putInt(bMsg.getBUserId());
        messageBuffer.put(bMsg.getMessage());
        byte[] encryptedMessage = cipher.encryptData(messageBuffer.array());

        //Initialize variables
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.wLen = encryptedMessage.length;
        this.bMsg = bMsg;
        this.packet = new byte[PACKET_LENGTH_WITHOUT_MESSAGE + wLen];

        ByteBuffer buffer = ByteBuffer.wrap(this.packet);
        buffer.mark();

        //Put variables into packet(byte array)
        buffer.put(bMagic);
        buffer.put(this.bSrc);
        buffer.putLong(this.bPktId);
        buffer.putInt(this.wLen);

        //Create first checksum
        byte[] bytesForFirstChecksum = new byte[BYTES_AMOUNT_FOR_FIRST_CHECKSUM];
        buffer.reset();
        buffer.get(bytesForFirstChecksum);
        this.wCrc16_first = CRC16.getCRC16(bytesForFirstChecksum);
        buffer.putShort(this.wCrc16_first);

        buffer.put(encryptedMessage);

        //Create second checksum
        this.wCrc16_second = CRC16.getCRC16(encryptedMessage);
        buffer.putShort(this.wCrc16_second);
    }

    /**
     * Constructor is used after receiving packet.
     * Packet is technically a byte array.
     * <p>
     * Packet must start with bMagic byte(13h).
     * Checksums(CRC16) are tested.
     * Message is decrypted with AES-GCM algorithm.
     *
     * @param bytes packet with appropriate data
     * @throws IllegalArgumentException packet doesn't start with magic byte(13h).
     *                                  Either first or second checksum is wrong.
     */
    public Packet(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.mark();

        if (buffer.get() != bMagic)
            throw new IllegalArgumentException("No start byte in byte array");

        this.packet = bytes;
        this.bSrc = buffer.get();
        this.bPktId = buffer.getLong();
        this.wLen = buffer.getInt();

        //Test first checksum
        byte[] bytesForFirstChecksum = new byte[BYTES_AMOUNT_FOR_FIRST_CHECKSUM];
        buffer.reset();
        buffer.get(bytesForFirstChecksum);
        this.wCrc16_first = buffer.getShort();
        if (this.wCrc16_first != CRC16.getCRC16(bytesForFirstChecksum))
            throw new IllegalArgumentException("Wrong first checksum");

        //Decrypt message - read
        byte[] encryptedMessage = new byte[wLen];
        buffer.get(encryptedMessage);

        //Test second checksum
        this.wCrc16_second = buffer.getShort();
        if (this.wCrc16_second != CRC16.getCRC16(encryptedMessage))
            throw new IllegalArgumentException("Wrong second checksum");

        //Decrypt message - decrypt, save
        byte[] decryptedMessage = cipher.decryptData(encryptedMessage);
        this.bMsg = new Message(ByteBuffer.wrap(decryptedMessage));
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
}
