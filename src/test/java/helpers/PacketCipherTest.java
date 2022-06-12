package helpers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PacketCipherTest {

    private final PacketCipher cipher = new PacketCipher();

    @Test
    void encryptData() {
        byte[] originalData = "123456789qwerty".getBytes();
        byte[] encryptedData = cipher.encryptData(originalData);
        assertFalse(Arrays.equals(originalData, encryptedData));
    }

    @Test
    void decryptData1() {
        byte[] originalData = "123456789qwerty".getBytes();
        byte[] encryptedData = cipher.encryptData(originalData);
        assertArrayEquals(originalData, cipher.decryptData(encryptedData));
    }

    @Test
    void decryptData2() {
        byte[] originalData = "abcdefghijklmnopqrstuvwxyz".getBytes();
        byte[] encryptedData = cipher.encryptData(originalData);
        assertArrayEquals(originalData, cipher.decryptData(encryptedData));
    }

    @Test
    void decryptData3() {
        byte[] originalData = "01234567890123456789012345".getBytes();
        byte[] encryptedData = cipher.encryptData(originalData);
        assertArrayEquals(originalData, cipher.decryptData(encryptedData));
    }
}