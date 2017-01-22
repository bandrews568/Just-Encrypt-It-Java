package us.brandonandrews.justencryptit;

import org.jasypt.util.text.BasicTextEncryptor;


public class Encryption {

    public String encrypt(String password, String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        if (password != null) {
            textEncryptor.setPassword(password);
        }
        String myEncryptedText = textEncryptor.encrypt(text);

        return myEncryptedText;
    }

    public String decrypt(String password, String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        if (password != null) {
            textEncryptor.setPassword(password);
        }
        String plainText = textEncryptor.decrypt(text);
        return plainText;
    }
}