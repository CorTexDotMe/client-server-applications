package helpers;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PacketCipher {

    private Cipher cipher;
    private SecretKey secretKey;
    private SecureRandom secureRandom;
    private final int keyLength = 128;
    private final String key = "SWv<sh]cwfP'3,`+";

    public PacketCipher() {
        try {
            //Create initialization vector
            secureRandom = new SecureRandom();
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            //Create key for cipher
            secretKey = generateSecretKey(key, iv);

            //Create cipher
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
