import helpers.CRC16;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class Packet {
    private static final int BYTES_AMOUNT_FOR_FIRST_CHECKSUM = 14;
    private static final int BYTES_OFFSET_FOR_FIRST_CHECKSUM = 0;
    private static final int BYTES_OFFSET_FOR_SECOND_CHECKSUM = 16;
    private static final int PACKET_LENGTH_WITHOUT_MESSAGE = 18;
    private static final byte bMagic = 0x13;

    private byte bSrc;
    private long bPktId;
    private int wLen;
    private short wCrc16_first;
    private Message bMsg;
    private short wCrc16_second;
    private byte[] packet;

    private Key key;
    private Cipher cipher;

    public Packet(byte bSrc, long bPktId, int wLen, Message bMsg)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        cipherInit();

        this.packet = new byte[PACKET_LENGTH_WITHOUT_MESSAGE + wLen];
        ByteBuffer buffer = ByteBuffer.wrap(this.packet);
        buffer.put(bMagic);
        buffer.put(bSrc);
        buffer.putLong(bPktId);
        buffer.putInt(wLen);

        byte[] bytesForFirstChecksum = new byte[BYTES_AMOUNT_FOR_FIRST_CHECKSUM];
        buffer.get(bytesForFirstChecksum,
                BYTES_OFFSET_FOR_FIRST_CHECKSUM,
                BYTES_AMOUNT_FOR_FIRST_CHECKSUM);
        buffer.putShort(CRC16.getCRC16(bytesForFirstChecksum));


        buffer.putInt(bMsg.getcType());
        buffer.putInt(bMsg.getbUserId());
        buffer.put(bMsg.getMessage());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipher.doFinal(packet, BYTES_OFFSET_FOR_SECOND_CHECKSUM, wLen, packet, BYTES_OFFSET_FOR_SECOND_CHECKSUM);

        byte[] messageByteArray = new byte[wLen];
        buffer.get(messageByteArray, BYTES_OFFSET_FOR_SECOND_CHECKSUM, wLen);
        buffer.putShort(CRC16.getCRC16(messageByteArray));
    }

    public Packet(byte[] bytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        cipherInit();

        this.packet = bytes;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        if (buffer.get() != bMagic)
            throw new IllegalArgumentException("No start byte in byte array");

        this.bSrc = buffer.get();
        this.bPktId = buffer.getLong();
        this.wLen = buffer.getInt();
        this.wCrc16_first = buffer.getShort();

        if (wrongChecksum(this.wCrc16_first, buffer, BYTES_OFFSET_FOR_FIRST_CHECKSUM, BYTES_AMOUNT_FOR_FIRST_CHECKSUM))
            throw new IllegalArgumentException("Wrong first checksum");

        byte[] message = new byte[this.wLen];
        this.cipher.init(Cipher.DECRYPT_MODE, this.key);
        this.cipher.doFinal(this.packet, BYTES_OFFSET_FOR_SECOND_CHECKSUM, this.wLen, message, 0);
        this.bMsg = new Message(ByteBuffer.wrap(message), this.wLen);

        int secondChecksumIndex = BYTES_OFFSET_FOR_SECOND_CHECKSUM + wLen;
        this.wCrc16_second = buffer.getShort(secondChecksumIndex);
        if (wrongChecksum(this.wCrc16_second, buffer, BYTES_OFFSET_FOR_SECOND_CHECKSUM, this.wLen))
            throw new IllegalArgumentException("Wrong second checksum");
    }

    public byte[] getPacket() {
        return packet;
    }

    private void cipherInit() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.key = generateKey();
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    private static Key generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize);
        return keyGenerator.generateKey();
    }

    private boolean wrongChecksum(short expectedChecksum, ByteBuffer buffer,
                                  int packetOffset, int packetToCheckLength) {
        byte[] bytesForChecksum = new byte[packetToCheckLength];
        buffer.get(bytesForChecksum, packetOffset, packetToCheckLength);
        return expectedChecksum != CRC16.getCRC16(bytesForChecksum);
    }
}
