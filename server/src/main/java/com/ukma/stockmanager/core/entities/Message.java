package com.ukma.stockmanager.core.entities;

import java.nio.ByteBuffer;

/**
 * Message structure:
 * Offset	Length	Mnemonic 	Notes
 * 00	    4	    cType	    Big-endian command code
 * 04	    4	    bUserId	    From whom the message was sent. There can be many clients in the system.
 *                              And one of many employees can work for each of these clients. big-endian
 * 08	    wLen-8	message	    Useful information, you can put JSON as an array of big-endian bytes
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
