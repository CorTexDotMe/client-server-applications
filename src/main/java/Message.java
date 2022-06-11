import java.nio.ByteBuffer;

/**
 * Структура повідомлення (message):
 * Offset	Length	Mnemonic 	Notes
 * 00	    4	    cType	    Код команди big-endian
 * 04	    4	    bUserId	    Від кого надіслане повідомлення.
 *                             В системі може бути багато клієнтів.
 *                             А на кожному з цих клієнтів може працювати один з багатьох працівників.
 *                             big-endian
 * 08	    wLen-8	message	    корисна інформація, можна покласти JSON як масив байтів big-endian
 *
 * @author Danylo Nechyporchuk
 */
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
