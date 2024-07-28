package com.ukma.stockmanager.network.implementation.tcp;

import com.ukma.stockmanager.core.utils.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TCPReceiver implements com.ukma.stockmanager.network.interfaces.Receiver {
    public final static BlockingQueue<byte[]> receivedPackets = new LinkedBlockingQueue<>();
    private final DataInputStream in;

    public TCPReceiver(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void receiveMessage() {
        try {
            byte bSrc = in.readByte();
            long bPktId = in.readLong();
            int wLen = in.readInt();
            byte[] remainder = new byte[Constants.BYTES_AMOUNT_OF_CRC +
                                        wLen +
                                        Constants.BYTES_AMOUNT_OF_CRC];
            if (in.read(remainder) < remainder.length)
                return;

            ByteBuffer packet = ByteBuffer.wrap(new byte[
                    Constants.BYTES_AMOUNT_FOR_FIRST_CHECKSUM +
                    remainder.length
                    ]);
            packet.put(Constants.bMagic).put(bSrc).putLong(bPktId).putInt(wLen);
            packet.put(remainder);

            receivedPackets.put(packet.array());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public byte[] poll() {
        try {
            byte[] result = null;
            while (result == null)
                result = receivedPackets.poll(Constants.POLL_TIMEOUT, TimeUnit.MILLISECONDS);
            return result;
        } catch (InterruptedException e) {
            return poll();
        }
    }
}
