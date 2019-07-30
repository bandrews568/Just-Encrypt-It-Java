package github.bandrews568.justencryptit.utils;

import android.util.Base64;

import org.jasypt.util.text.BasicTextEncryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    public static String encrypt(String password, String text, String algorithm) throws Exception {
        switch (algorithm) {
            case "Basic":
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword(password);
                return textEncryptor.encrypt(text);
            case "AES":
                Key aesKey = new SecretKeySpec(password.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
                return Base64.encodeToString(encrypted, Base64.DEFAULT);
            default:
                return "";
        }
    }

    public static String decrypt(String password, String text, String algorithm) throws Exception {
        switch (algorithm) {
            case "Basic":
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword(password);
                return textEncryptor.decrypt(text);
            case "AES":
                Key aesKey = new SecretKeySpec(password.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] decrypted = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
                return new String(decrypted);
            default:
                return "";
        }
    }

    public static void encryptFile(String password, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, password, inputFile, outputFile);
    }

    public static void decryptFile(String password, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, password, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    public static String getFileBaseName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }
}