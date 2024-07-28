package com.ukma.stockmanager.utils;

import com.ukma.stockmanager.core.security.Decryptor;
import com.ukma.stockmanager.core.security.Encryptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PacketCipherTest {

    @Test
    void encryptData() {
        byte[] originalData = "123456789qwerty".getBytes();
        byte[] encryptedData = Encryptor.encrypt(originalData);
        assertFalse(Arrays.equals(originalData, encryptedData));
    }

    @Test
    void decryptData1() {
        byte[] originalData = "123456789qwerty".getBytes();
        byte[] encryptedData = Encryptor.encrypt(originalData);
        assertArrayEquals(originalData, Decryptor.decrypt(encryptedData));
    }

    @Test
    void decryptData2() {
        byte[] originalData = "abcdefghijklmnopqrstuvwxyz".getBytes();
        byte[] encryptedData = Encryptor.encrypt(originalData);
        assertArrayEquals(originalData, Decryptor.decrypt(encryptedData));
    }

    @Test
    void decryptData3() {
        byte[] originalData = "01234567890123456789012345".getBytes();
        byte[] encryptedData = Encryptor.encrypt(originalData);
        assertArrayEquals(originalData, Decryptor.decrypt(encryptedData));
    }
}