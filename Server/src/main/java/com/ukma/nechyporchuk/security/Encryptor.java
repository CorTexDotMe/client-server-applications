package com.ukma.nechyporchuk.security;

public class Encryptor {
    private static final PacketCipher cipher = PacketCipher.getInstance();

    public static byte[] encrypt(byte[] message) {
        synchronized (cipher) {
            return cipher.encryptData(message);
        }
    }
}
