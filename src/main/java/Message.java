import java.nio.ByteBuffer;

public class Message {
    private int cType;
    private int bUserId;
    private byte[] message;

    public Message(ByteBuffer buffer, int wLen) {
        cType = buffer.getInt();
        bUserId = buffer.getInt();
        message = new byte[wLen - Integer.BYTES * 2];
        buffer.get(message, Integer.BYTES * 2, wLen);
    }

    public int getcType() {
        return cType;
    }

    public int getbUserId() {
        return bUserId;
    }

    public byte[] getMessage() {
        return message;
    }
}
