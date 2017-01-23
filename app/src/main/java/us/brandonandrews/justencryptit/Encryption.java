package us.brandonandrews.justencryptit;

import org.jasypt.util.text.BasicTextEncryptor;


public class Encryption {

    public static String encrypt(String password, String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);
        String myEncryptedText = textEncryptor.encrypt(text);

        return myEncryptedText;
    }

    public static String decrypt(String password, String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);
        String plainText = textEncryptor.decrypt(text);
        return plainText;
    }
}