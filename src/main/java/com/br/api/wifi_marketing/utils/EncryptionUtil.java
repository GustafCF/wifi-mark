package com.br.api.wifi_marketing.utils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;

public class EncryptionUtil {

    @Value("${classpath:newkey.pub}")
    private RSAPublicKey publicKey;
    @Value("${classpath:newkey.key}")
    private RSAPrivateKey privateKey;

    public EncryptionUtil(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    // Método para gerar uma chave AES
    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Você pode escolher 128, 192 ou 256 bits
        return keyGen.generateKey();
    }

    // Método para criptografar a chave AES usando RSA
    public byte[] encryptAESKey(SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }

    // Método para descriptografar a chave AES usando RSA
    public SecretKey decryptAESKey(byte[] encryptedAESKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = cipher.doFinal(encryptedAESKey);
        return new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
    }

    // Método para criptografar dados usando AES
    public byte[] encryptData(SecretKey aesKey, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.doFinal(data.getBytes());
    }

    // Método para descriptografar dados usando AES
    public String decryptData(SecretKey aesKey, byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData);
    }
}
