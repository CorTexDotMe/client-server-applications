package com.ukma.nechyporchuk.security;

public class Encryptor {
    private static final PacketCipher cipher = new PacketCipher();

    public static byte[] encrypt(byte[] message) {
        return cipher.encryptData(message);
    }
}
