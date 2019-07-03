package us.brandonandrews.justencryptit.ui.encryptor;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import us.brandonandrews.justencryptit.utils.Encryption;


public class EncryptorViewModel extends ViewModel {
    private MutableLiveData<EncryptionResult> encryptionLiveData = new MutableLiveData<>();

    public void encryptText(String text, String password) {
        new AsyncEncryption().execute(text, password, EncryptionType.ENCRYPT.toString());
    }

    public void decryptText(String text, String password) {
        new AsyncEncryption().execute(text, password, EncryptionType.DECRYPT.toString());
    }

    public MutableLiveData<EncryptionResult> getEncryptionLiveData() {
        return encryptionLiveData;
    }

    private class AsyncEncryption extends AsyncTask<String, Void, EncryptionResult> {

        @Override
        protected EncryptionResult doInBackground(String... strings) {
            String text = strings[0];
            String password = strings[1];
            String type = strings[2];

            EncryptionResult encryptionResult = new EncryptionResult();

            try {
                switch (EncryptionType.valueOf(type)) {
                    case ENCRYPT:
                        encryptionResult.setText(Encryption.encrypt(password, text));
                        break;
                    case DECRYPT:
                        encryptionResult.setText(Encryption.decrypt(password, text));
                        break;
                }
            } catch (Exception e) {
                encryptionResult.setError(e);
            }

            return encryptionResult;
        }

        @Override
        protected void onPostExecute(EncryptionResult encryptionResult) {
            encryptionLiveData.postValue(encryptionResult);
        }
    }
}