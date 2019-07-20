package github.bandrews568.justencryptit.utils;

import android.util.Base64;

import org.jasypt.util.text.BasicTextEncryptor;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    public static String encrypt(String password, String text, String algorithm) {
        switch (algorithm) {
            case "Basic":
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword(password);
                return textEncryptor.encrypt(text);
            case "AES":
                try {
                Key aesKey = new SecretKeySpec(password.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
                return Base64.encodeToString(encrypted, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            default:
                return "";
        }
    }

    public static String decrypt(String password, String text, String algorithm) {
        switch (algorithm) {
            case "Basic":
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword(password);
                return textEncryptor.decrypt(text);
            case "AES":
                try {
                    Key aesKey = new SecretKeySpec(password.getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, aesKey);
                    byte[] decrypted = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
                    return new String(decrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            default:
                return "";
        }
    }
}