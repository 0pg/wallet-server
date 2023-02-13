package com.example.wallet.server.utils;

import com.example.wallet.domain.entities.EthWallet;
import com.example.wallet.server.exception.InvalidInput;
import lombok.NonNull;
import org.web3j.crypto.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
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
            byte[] encryptedPK = cipher.doFinal(credentials.getEcKeyPair().getPrivateKey().toByteArray());
            String pKey = Base64.getEncoder().encodeToString(encryptedPK);
            return Base64.getEncoder().encodeToString((iv.concat(pKey)).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new InvalidInput("create secret", e);
        }
    }

    public static String extractPKey(String password, String text) {
        try {
            byte[] decoded = Base64.getDecoder().decode(text);
            String decodedString = new String(decoded, StandardCharsets.UTF_8);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedString.substring(0, 16).getBytes());
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
            byte[] decodedPK = Base64.getDecoder().decode(decodedString.substring(16).getBytes(StandardCharsets.UTF_8));
            byte[] pk = cipher.doFinal(decodedPK);
            return new BigInteger(pk).toString(16);
        } catch (Exception e) {
            throw new InvalidInput("decode secret", e);
        }
    }
}
