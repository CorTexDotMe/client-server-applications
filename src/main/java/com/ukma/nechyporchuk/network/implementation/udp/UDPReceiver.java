package com.ukma.nechyporchuk.network.implementation.udp;

import com.ukma.nechyporchuk.utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Create packet out of thin air.
 * Random class is used
 *
 * @author Danylo Nechyporchuk
 */
public class UDPReceiver implements com.ukma.nechyporchuk.network.interfaces.Receiver {

    private DatagramPacket datagramPacket;
    private byte[] buf = new byte[256];
    public UDPReceiver(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }

    @Override
    public byte[] receiveMessage() {
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


                    return packet.array();
                }
            }
            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
        } catch (BufferUnderflowException e) {
            return null;
        }
    }
}
