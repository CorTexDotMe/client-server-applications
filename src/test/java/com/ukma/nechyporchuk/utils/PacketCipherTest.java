package com.ukma.nechyporchuk.utils;

import com.ukma.nechyporchuk.security.Decryptor;
import com.ukma.nechyporchuk.security.Encryptor;
import com.ukma.nechyporchuk.security.PacketCipher;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PacketCipherTest {

    @Test
    void encryptData() {
        byte[] originalData = "123456789qwerty".getBytes();
        byte[] encryptedData = PacketCipher.getInstance().encryptData(originalData);
        assertFalse(Arrays.equals(originalData, encryptedData));
    }

    @Test
    void decryptData1() {
        byte[] originalData = "123456789qwerty".getBytes();
        byte[] encryptedData = PacketCipher.getInstance().encryptData(originalData);
        assertArrayEquals(originalData, PacketCipher.getInstance().decryptData(encryptedData));
    }

    @Test
    void decryptData2() {
        byte[] originalData = "abcdefghijklmnopqrstuvwxyz".getBytes();
        byte[] encryptedData = PacketCipher.getInstance().encryptData(originalData);
        assertArrayEquals(originalData, PacketCipher.getInstance().decryptData(encryptedData));
    }

    @Test
    void decryptData3() {
        byte[] originalData = "01234567890123456789012345".getBytes();
        byte[] encryptedData = PacketCipher.getInstance().encryptData(originalData);
        assertArrayEquals(originalData, PacketCipher.getInstance().decryptData(encryptedData));
    }
}