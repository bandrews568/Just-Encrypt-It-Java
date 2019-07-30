package github.bandrews568.justencryptit.ui.encryptor;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import github.bandrews568.justencryptit.model.EncryptionTextResult;
import github.bandrews568.justencryptit.model.EncryptionType;
import github.bandrews568.justencryptit.utils.Encryption;
import github.bandrews568.justencryptit.utils.SingleLiveEvent;


public class EncryptorViewModel extends ViewModel {
    private SingleLiveEvent<EncryptionTextResult> encryptionLiveData = new SingleLiveEvent<>();

    public void encryptText(String text, String password, String algorithm) {
        new AsyncEncryption().execute(text, password, EncryptionType.ENCRYPT.toString(), algorithm);
    }

    public void decryptText(String text, String password, String algorithm) {
        new AsyncEncryption().execute(text, password, EncryptionType.DECRYPT.toString(), algorithm);
    }

    public MutableLiveData<EncryptionTextResult> getEncryptionLiveData() {
        return encryptionLiveData;
    }

    private class AsyncEncryption extends AsyncTask<String, Void, EncryptionTextResult> {

        @Override
        protected EncryptionTextResult doInBackground(String... strings) {
            String text = strings[0];
            String password = strings[1];
            String type = strings[2];
            String algorithm = strings[3];

            EncryptionTextResult encryptionTextResult = new EncryptionTextResult();

            try {
                switch (EncryptionType.valueOf(type)) {
                    case ENCRYPT:
                        encryptionTextResult.setText(Encryption.encrypt(password, text, algorithm));
                        break;
                    case DECRYPT:
                        encryptionTextResult.setText(Encryption.decrypt(password, text, algorithm));
                        break;
                }
            } catch (Exception e) {
                encryptionTextResult.setError(e);
            }

            return encryptionTextResult;
        }

        @Override
        protected void onPostExecute(EncryptionTextResult encryptionTextResult) {
            encryptionLiveData.postValue(encryptionTextResult);
        }
    }
}