package com.ukma.nechyporchuk.core;

public class ProcessorMessage {

    private byte bSrc;
    private long bPktId;
    private Message message;

    public ProcessorMessage(byte bSrc, long bPktId, Message message) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.message = message;
    }

    public byte getbSrc() {
        return bSrc;
    }

    public long getbPktId() {
        return bPktId;
    }

    public Message getMessage() {
        return message;
    }
}
