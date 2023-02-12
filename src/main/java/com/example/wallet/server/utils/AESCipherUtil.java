package com.example.wallet.server.utils;

import com.example.wallet.domain.entities.EthWallet;
import lombok.NonNull;
import org.web3j.crypto.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class AESCipherUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String generateSecret(@NonNull String password, @NonNull WalletFile walletFile) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            Credentials credentials = Credentials.create(Wallet.decrypt(password, walletFile));
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), "AES");
            String iv = UUID.randomUUID().toString().substring(0, 16);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            String secret = iv.concat(new String(credentials.getEcKeyPair().getPrivateKey().toByteArray()));

            byte[] encrypted = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractPKey(String password, String text) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(text.substring(0, 16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            byte[] decoded = Base64.getDecoder().decode(text.substring(16));
            byte[] pk = cipher.doFinal(decoded);
            return new String(pk, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
