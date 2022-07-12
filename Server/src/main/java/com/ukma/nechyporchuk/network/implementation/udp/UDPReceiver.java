package com.ukma.nechyporchuk.network.implementation.udp;

import com.ukma.nechyporchuk.core.utils.Constants;
import com.ukma.nechyporchuk.processor.Controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Danylo Nechyporchuk
 */
public class UDPReceiver implements com.ukma.nechyporchuk.network.interfaces.Receiver {

    public final static BlockingQueue<byte[]> receivedPackets = new LinkedBlockingQueue<>();
    private final DatagramSocket socket;
    private final DatagramPacket datagramPacket;

    public UDPReceiver(DatagramSocket socket, DatagramPacket datagramPacket) {
        this.socket = socket;
        this.datagramPacket = datagramPacket;
    }

    @Override
    public void receiveMessage() {
        try {
            ByteBuffer receivedBytes = ByteBuffer.wrap(datagramPacket.getData());

            while (receivedBytes.remaining() > 0) {
                if (receivedBytes.get() == Constants.bMagic) {
                    byte bSrc = receivedBytes.get();
                    long bPktId = receivedBytes.getLong();
                    int wLen = receivedBytes.getInt();
                    byte[] remainder = new byte[Constants.BYTES_AMOUNT_OF_CRC +
                                                wLen +
                                                Constants.BYTES_AMOUNT_OF_CRC];
                    receivedBytes.get(remainder);

                    ByteBuffer packet = ByteBuffer.wrap(new byte[
                            Constants.BYTES_AMOUNT_FOR_FIRST_CHECKSUM +
                            remainder.length
                            ]);
                    packet.put(Constants.bMagic).put(bSrc).putLong(bPktId).putInt(wLen);
                    packet.put(remainder);


                    receivedPackets.put(packet.array());
                    Controller.getInstance().workWithUDPPacket(this, socket, datagramPacket);
                }
            }
        } catch (BufferUnderflowException ignored) {

        } catch (Exception e) {
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
