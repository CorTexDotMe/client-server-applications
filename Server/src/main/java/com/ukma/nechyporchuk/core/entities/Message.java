package com.ukma.nechyporchuk.core.entities;

import java.nio.ByteBuffer;

/**
 * Структура повідомлення (message):
 * Offset	Length	Mnemonic 	Notes
 * 00	    4	    cType	    Код команди big-endian
 * 04	    4	    bUserId	    Від кого надіслане повідомлення. В системі може бути багато клієнтів.
 * А на кожному з цих клієнтів може працювати один з багатьох працівників. big-endian
 * 08	    wLen-8	message	    корисна інформація, можна покласти JSON як масив байтів big-endian
 *
 * @author Danylo Nechyporchuk
 */
public class Message {
    private int cType;
    private int bUserId;
    private byte[] message;

    /**
     * Constructor to create message with byte buffer that contains only message.
     * Required data in message is:
     * cType - int with command
     * bUserId - int with user id
     * message - byte array with additional information
     *
     * @param buffer byte buffer with two ints and byte array.
     */
    public Message(ByteBuffer buffer) {
        this.cType = buffer.getInt();
        this.bUserId = buffer.getInt();
        this.message = new byte[buffer.remaining()];
        buffer.get(this.message);
    }

    /**
     * Constructor to create message straight with data. Is used while packet is sent.
     *
     * @param cType   int with command
     * @param bUserId int with user id
     * @param message byte array with additional information
     */
    public Message(int cType, int bUserId, byte[] message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    public int getCType() {
        return cType;
    }

    public int getBUserId() {
        return bUserId;
    }

    public byte[] getMessage() {
        return message;
    }
}
