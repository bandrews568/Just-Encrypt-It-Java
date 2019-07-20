package github.bandrews568.justencryptit.ui.encryptor;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import github.bandrews568.justencryptit.utils.Encryption;
import github.bandrews568.justencryptit.utils.SingleLiveEvent;


public class EncryptorViewModel extends ViewModel {
    private SingleLiveEvent<EncryptionResult> encryptionLiveData = new SingleLiveEvent<>();

    public void encryptText(String text, String password, String algorithm) {
        new AsyncEncryption().execute(text, password, EncryptionType.ENCRYPT.toString(), algorithm);
    }

    public void decryptText(String text, String password, String algorithm) {
        new AsyncEncryption().execute(text, password, EncryptionType.DECRYPT.toString(), algorithm);
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
            String algorithm = strings[3];

            EncryptionResult encryptionResult = new EncryptionResult();

            try {
                switch (EncryptionType.valueOf(type)) {
                    case ENCRYPT:
                        encryptionResult.setText(Encryption.encrypt(password, text, algorithm));
                        break;
                    case DECRYPT:
                        encryptionResult.setText(Encryption.decrypt(password, text, algorithm));
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