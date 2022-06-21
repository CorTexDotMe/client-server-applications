package com.ukma.nechyporchuk.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Cipher that use AES-GCM algorithm to encrypt any byte array.
 * Password is used to create secretKey for cipher
 * <p>
 * Initialization vector for cipher is 12 byte long. SecureRandom is used for creating
 * Encrypted array is longer than original(decrypted).
 * Encrypted array store initialization vector and encrypted data.
 *
 * @author Danylo Nechyporchuk
 */
public class PacketCipher {

    private static PacketCipher INSTANCE;
    private Cipher cipher;
    private SecretKey secretKey;
    private SecureRandom secureRandom;
    private final int keyLength = 128;
    private final String password = "SWv<sh]cwfP'3,`+";

    /**
     * Initialize:
     * 1. 12 byte long initialization vector using SecureRandom.
     * 2. Secret key using password and initialization vector
     * 3. Cipher with appropriate algorithm(AES/GCM/NoPadding).
     */
    private PacketCipher() {
        try {
            //Create initialization vector
            secureRandom = new SecureRandom();
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            //Create key for cipher
            secretKey = generateSecretKey(password, iv);

            //Create cipher
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PacketCipher getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PacketCipher();

        return INSTANCE;
    }

    /**
     * Create secret key to use it with cipher.
     *
     * @param password             string that will be used to create key
     * @param initializationVector byte array that will be used to create key
     * @return secret key for cipher
     */
    public static SecretKey generateSecretKey(String password, byte[] initializationVector) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), initializationVector, 65536, 128); // AES-128
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypt data with AES-GCM algorithm
     * Encrypted data is longer that original: initialization vector + encrypted data.
     *
     * @param data byte array that will be encrypted
     * @return encrypted byte array
     */
    public byte[] encryptData(byte[] data) {
        try {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(keyLength, iv));

            byte[] encryptedData = cipher.doFinal(data);

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            buffer.put(iv);
            buffer.put(encryptedData);
            return buffer.array();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt data with AES-GCM algorithm
     * Decrypted data is shorter that encrypted.
     *
     * @param data byte array that will be decrypted
     * @return decrypted byte array
     */
    public byte[] decryptData(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);

            byte[] iv = new byte[12];
            buffer.get(iv);
            byte[] encryptedData = new byte[buffer.remaining()];
            buffer.get(encryptedData);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(keyLength, iv));
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
