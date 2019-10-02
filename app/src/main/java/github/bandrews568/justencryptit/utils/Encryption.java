package github.bandrews568.justencryptit.utils;

import android.util.Base64;

import androidx.lifecycle.MutableLiveData;

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

    public static void encryptFile(String password, File inputFile, File outputFile, MutableLiveData<Integer> cryptoProgressLiveData) throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, password, inputFile, outputFile, cryptoProgressLiveData);
    }

    public static void decryptFile(String password, File inputFile, File outputFile, MutableLiveData<Integer> cryptoProgressLiveData) throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, password, inputFile, outputFile, cryptoProgressLiveData);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile, MutableLiveData<Integer> cryptoProgressLiveData) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] input = new byte[2048];
            int bytesRead;

            long totalFileSize = inputFile.length();
            double percentUnit = 100.0 / totalFileSize;
            long readLength = 0;

            while ((bytesRead = inputStream.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                readLength += bytesRead;
                cryptoProgressLiveData.postValue((int) Math.round(percentUnit * readLength));

                if (output != null) {
                    outputStream.write(output);
                }
            }

            byte[] output = cipher.doFinal();
            if (output != null)
                outputStream.write(output);

            cryptoProgressLiveData.postValue(100);

            inputStream.close();
            outputStream.flush();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}