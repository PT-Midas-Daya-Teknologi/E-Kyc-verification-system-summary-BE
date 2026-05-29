package com.midasteknologi.e_kyc_verification_summary.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

@Slf4j
public class EncryptionUtil {

    public static String encryptAes(String data, String secretKey, String ivKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(secretKey), getIvKey(ivKey));
            byte[] cipherText = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Exception while doing AES encryption: ", e);
            return null;
        }
    }

    public static String decryptAes(String encryptedData, String secretKey, String ivKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secretKey), getIvKey(ivKey));
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Exception while doing AES decryption: ", e);
            return null;
        }
    }

    public static SecretKey getSecretKey(String secretKey) {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    }

    public static AlgorithmParameterSpec getIvKey(String ivKey) {
        return new GCMParameterSpec(128, ivKey.getBytes());
    }
}
