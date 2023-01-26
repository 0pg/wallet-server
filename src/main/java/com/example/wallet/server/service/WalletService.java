package com.example.wallet.server.service;

import org.springframework.stereotype.Service;
import org.web3j.crypto.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class WalletService {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private WalletFile generateWallet(String password) {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            return Wallet.createLight(password, keyPair);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateSecret(String password, WalletFile walletFile) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            Credentials credentials = Credentials.create(Wallet.decrypt(password, walletFile));
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
            String iv = UUID.randomUUID().toString().substring(0, 16);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            String secret = iv.concat(new String(credentials.getEcKeyPair().getPrivateKey().toByteArray()));

            byte[] encrypted = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Credentials getCredentials(String password, String text) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(text.substring(0, 16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            byte[] decoded = Base64.getDecoder().decode(text.substring(16));
            byte[] pk = cipher.doFinal(decoded);
            return Credentials.create(new String(pk, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
