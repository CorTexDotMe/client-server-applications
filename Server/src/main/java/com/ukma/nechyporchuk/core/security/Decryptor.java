package com.ukma.nechyporchuk.core.security;

public class Decryptor {
    private static final PacketCipher cipher = PacketCipher.getInstance();

    public static byte[] decrypt(byte[] message) {
        synchronized (cipher) {
            return cipher.decryptData(message);
        }
    }
}
