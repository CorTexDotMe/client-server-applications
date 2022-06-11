import helpers.CRC16;

import java.nio.ByteBuffer;

public class PacketToSend {
    private final int packetLengthWithoutMessage = 18;
    private byte[] packet;

    public PacketToSend(byte bSrc, long bPktId, int wLen, Message bMsg) {
        packet = new byte[packetLengthWithoutMessage + wLen];
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        buffer.put(Packet.bMagic);
        buffer.put(bSrc);
        buffer.putLong(bPktId);
        buffer.putInt(wLen);

        byte[] bytesForFirstChecksum = new byte[Packet.BYTES_AMOUNT_FOR_FIRST_CHECKSUM];
        buffer.get(bytesForFirstChecksum,
                Packet.BYTES_OFFSET_FOR_FIRST_CHECKSUM,
                Packet.BYTES_AMOUNT_FOR_FIRST_CHECKSUM);
        buffer.putShort(CRC16.getCRC16(bytesForFirstChecksum));

        buffer.putInt(bMsg.getcType());
        buffer.putInt(bMsg.getbUserId());
        buffer.put(bMsg.getMessage());

        byte[] messageByteArray = new byte[wLen];
        buffer.get(messageByteArray, Packet.BYTES_OFFSET_FOR_SECOND_CHECKSUM, wLen);
        buffer.putShort(CRC16.getCRC16(messageByteArray));
    }

    public byte[] getPacket() {
        return packet;
    }
}
