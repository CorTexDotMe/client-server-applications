package com.ukma.nechyporchuk.network.implementation.tcp;

import com.ukma.nechyporchuk.core.CommandAnalyser;
import com.ukma.nechyporchuk.core.Controller;
import com.ukma.nechyporchuk.core.Message;
import com.ukma.nechyporchuk.core.Packet;
import com.ukma.nechyporchuk.utils.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Create packet out of thin air.
 * Random class is used
 *
 * @author Danylo Nechyporchuk
 */
public class TCPReceiver implements com.ukma.nechyporchuk.network.interfaces.Receiver {

    private DataInputStream in;

    public TCPReceiver(DataInputStream in) {
        this.in = in;
    }

    @Override
    public byte[] receiveMessage() {
//            byte bSrc = in.readByte();
//            long bPktId = in.readLong();
//            int wLen = in.readInt();
//            short wCrc16_first = in.readShort();
//            ByteBuffer packet = ByteBuffer.wrap(new byte[
//                    Constants.BYTES_AMOUNT_FOR_FIRST_CHECKSUM +
//                    Constants.BYTES_AMOUNT_OF_CRC +
//                    wLen +
//                    Constants.BYTES_AMOUNT_OF_CRC
//                    ]);
//            packet.put(Constants.bMagic).put(bSrc).putLong(bPktId).putInt(wLen).putShort(wCrc16_first);
//            packet.put(in.readNBytes(wLen));
//            short wCrc16_second = in.readShort();
//            packet.putShort(wCrc16_second);
        try {
            byte bSrc = in.readByte();
            long bPktId = in.readLong();
            int wLen = in.readInt();
            byte[] remainder = new byte[Constants.BYTES_AMOUNT_OF_CRC +
                                        wLen +
                                        Constants.BYTES_AMOUNT_OF_CRC];
            if (in.read(remainder) < remainder.length)
                return null;

            ByteBuffer packet = ByteBuffer.wrap(new byte[
                    Constants.BYTES_AMOUNT_FOR_FIRST_CHECKSUM +
                    remainder.length
                    ]);
            packet.put(Constants.bMagic).put(bSrc).putLong(bPktId).putInt(wLen);
            packet.put(remainder);


            return packet.array();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long bPktId = 0;
}
