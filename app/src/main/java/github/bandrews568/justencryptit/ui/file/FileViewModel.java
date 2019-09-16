package github.bandrews568.justencryptit.ui.file;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.FileObserver;

import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.Set;

import github.bandrews568.justencryptit.model.EncryptFileWork;
import github.bandrews568.justencryptit.model.EncryptionFileResult;
import github.bandrews568.justencryptit.model.FileListItem;
import github.bandrews568.justencryptit.utils.CryptoException;
import github.bandrews568.justencryptit.utils.Encryption;
import github.bandrews568.justencryptit.utils.SingleLiveEvent;

public class FileViewModel extends ViewModel {

    private SingleLiveEvent<EncryptionFileResult> encryptionLiveData = new SingleLiveEvent<>();

    private MutableLiveData<Set<FileListItem>> encryptedFilesLiveData = new MutableLiveData<>();
    private MutableLiveData<Set<FileListItem>> decryptedFilesLiveData = new MutableLiveData<>();

    private Set<FileListItem> encryptedFilesSet = new ArraySet<>();
    private Set<FileListItem> decryptedFilesSet = new ArraySet<>();

    private File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

    private int fileEvents = (FileObserver.CREATE | FileObserver.DELETE | FileObserver.MOVED_TO);

    private FileObserver fileObserver = new FileObserver(directory.getPath(), fileEvents) {
        @Override
        public void onEvent(int event, @Nullable String path) {
            updateFileSets();
        }
    };

    public void encryptFile(String password, File inputFile, File outputFile) {
        EncryptFileWork encryptFileWork = new EncryptFileWork(password, inputFile, outputFile);
        new AsyncFileEncryption().execute(encryptFileWork);
    }

    private void updateFileSets() {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        if (!directory.exists() || directory.listFiles() == null) return;

        for (File file : directory.listFiles()) {
            FileListItem fileListItem = new FileListItem();
            fileListItem.setLocation(file.getPath());
            fileListItem.setTime(file.lastModified());
            fileListItem.setFilename(file.getName());
            fileListItem.setSize(file.length());

            // Need to check the file extensions to include only .jei files
            if (file.getName().endsWith(".jei")) {
                // Add the file to the encrypted set
                encryptedFilesSet.add(fileListItem);
            } else {
                // Add the file to the decrypted set
                decryptedFilesSet.add(fileListItem);
            }
        }
    }

    public SingleLiveEvent<EncryptionFileResult> getEncryptionLiveData() {
        return encryptionLiveData;
    }

    public MutableLiveData<Set<FileListItem>> getEncryptedFilesLiveData() {
        return encryptedFilesLiveData;
    }

    public MutableLiveData<Set<FileListItem>> getDecryptedFilesLiveData() {
        return decryptedFilesLiveData;
    }

    public Set<FileListItem> getEncryptedFilesSet() {
        return encryptedFilesSet;
    }

    public Set<FileListItem> getDecryptedFilesSet() {
        return decryptedFilesSet;
    }

    public FileObserver getFileObserver() {
        return fileObserver;
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
}
