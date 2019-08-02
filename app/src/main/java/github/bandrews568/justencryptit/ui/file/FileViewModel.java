package github.bandrews568.justencryptit.ui.file;

import android.os.AsyncTask;

import androidx.lifecycle.ViewModel;

import java.io.File;

import github.bandrews568.justencryptit.model.EncryptFileWork;
import github.bandrews568.justencryptit.model.EncryptionFileResult;
import github.bandrews568.justencryptit.utils.CryptoException;
import github.bandrews568.justencryptit.utils.Encryption;
import github.bandrews568.justencryptit.utils.SingleLiveEvent;

public class FileViewModel extends ViewModel {

    private SingleLiveEvent<EncryptionFileResult> encryptionLiveData = new SingleLiveEvent<>();

    public void encryptFile(String password, File inputFile, File outputFile) {
        EncryptFileWork encryptFileWork = new EncryptFileWork(password, inputFile, outputFile);
        new AsyncFileEncryption().execute(encryptFileWork);
    }

    private class AsyncFileEncryption extends AsyncTask<EncryptFileWork, Void, EncryptionFileResult> {

        @Override
        protected EncryptionFileResult doInBackground(EncryptFileWork... params) {
            EncryptFileWork encryptFileWork = params[0];
            EncryptionFileResult encryptionFileResult = new EncryptionFileResult();

            try{
                Encryption.encryptFile(encryptFileWork.getPassword(),
                        encryptFileWork.getInputFile(),
                        encryptFileWork.getOutputFile());
            } catch (CryptoException e) {
                encryptionFileResult.setError(e);
            }

            return encryptionFileResult;
        }

        @Override
        protected void onPostExecute(EncryptionFileResult encryptionFileResult) {
            encryptionLiveData.postValue(encryptionFileResult);
        }
    }

    public SingleLiveEvent<EncryptionFileResult> getEncryptionLiveData() {
        return encryptionLiveData;
    }
}
