package com.ukma.nechyporchuk.security;

public class Decryptor {
    private static final PacketCipher cipher = new PacketCipher();

    public static byte[] decrypt(byte[] message) {
        return cipher.decryptData(message);
    }
}
