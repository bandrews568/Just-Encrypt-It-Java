package us.brandonandrews.justencryptit.utils;

import org.jasypt.util.text.BasicTextEncryptor;


public class Encryption {

    public static String encrypt(String password, String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);
        return textEncryptor.encrypt(text);
    }

    public static String decrypt(String password, String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);
        return textEncryptor.decrypt(text);
    }
}