package com.ukma.nechyporchuk.utils;

public class Constants {
    public static final int PACKET_LENGTH_WITHOUT_MESSAGE = 18;
    public static final int BYTES_AMOUNT_FOR_FIRST_CHECKSUM = 14;
    public static final int BYTES_AMOUNT_OF_CRC = 2;
    public static final long POLL_TIMEOUT = 500;
    public static final byte bMagic = 0x13;
    public static final byte bEnd = 0x0f;
    public static final byte[] bEndMessage = {bEnd, bEnd, bEnd, bEnd, bEnd};

    public static final int TCP_PORT = 6666;
    public static final int UDP_PORT = 1337;

    public static long bPktIdForTesting = 0;
}
