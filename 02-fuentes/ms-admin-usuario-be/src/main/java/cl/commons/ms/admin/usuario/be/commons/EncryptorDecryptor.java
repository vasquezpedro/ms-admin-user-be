package cl.commons.ms.admin.usuario.be.commons;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EncryptorDecryptor {
    
    @Value("${encryptor.encryptor.secretKey}")
    private String secretKey;

    @Value("${encryptor.encryptor.initVector}")
    private String initVector;

    public String encrypt(String input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            byte[] encrypted = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            log.error("Error encrypt:"+ex.getMessage(),ex);
        }

        return null;
    }

    public String decrypt(String encryptedInput) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedInput));
            return new String(original);
        } catch (Exception ex) {
            log.error("Error decrypt:"+ex.getMessage(),ex);
        }

        return null;
    }

}