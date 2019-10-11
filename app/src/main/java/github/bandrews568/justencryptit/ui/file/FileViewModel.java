package github.bandrews568.justencryptit.ui.file;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.FileObserver;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import github.bandrews568.justencryptit.model.EncryptFileWork;
import github.bandrews568.justencryptit.model.EncryptionFileResult;
import github.bandrews568.justencryptit.model.FileListItem;
import github.bandrews568.justencryptit.utils.CryptoException;
import github.bandrews568.justencryptit.utils.Encryption;
import github.bandrews568.justencryptit.utils.SingleLiveEvent;

public class FileViewModel extends ViewModel {

    private SingleLiveEvent<EncryptionFileResult> encryptionLiveData = new SingleLiveEvent<>();
    private SingleLiveEvent<EncryptionFileResult> decryptedLiveData = new SingleLiveEvent<>();
    private SingleLiveEvent<EncryptionFileResult> encryptionActionLiveData = new SingleLiveEvent<>();

    private MutableLiveData<List<FileListItem>> encryptedFilesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<FileListItem>> decryptedFilesLiveData = new MutableLiveData<>();

    private MutableLiveData<Integer> cryptoProgressLiveData = new MutableLiveData<>();

    private List<FileListItem> encryptedFilesList = new ArrayList<>();
    private List<FileListItem> decryptedFilesList = new ArrayList<>();

    private File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

    private AtomicBoolean backgroundFileWork = new AtomicBoolean(false);

    private int fileEvents = (FileObserver.CREATE |
                              FileObserver.DELETE |
                              FileObserver.DELETE_SELF |
                              FileObserver.MODIFY |
                              FileObserver.MOVED_FROM |
                              FileObserver.MOVED_TO |
                              FileObserver.MOVE_SELF);

    private FileObserver fileObserver = new FileObserver(directory.getPath(), fileEvents) {
        @Override
        public void onEvent(int event, @Nullable String path) {
            if (!backgroundFileWork.get()) {
                updateFiles();
            }
        }
    };

    public AsyncFileEncryption encryptFile(String password, File inputFile, File outputFile) {
        EncryptFileWork encryptFileWork = new EncryptFileWork(password, inputFile, outputFile);

        AsyncFileEncryption asyncFileEncryption = new AsyncFileEncryption();
        asyncFileEncryption.setLiveData(encryptionLiveData);
        asyncFileEncryption.execute(encryptFileWork);

        return asyncFileEncryption;
    }

    public AsyncFileEncryption encryptFileAction(String password, File inputFile, File outputFile) {
        EncryptFileWork encryptFileWork = new EncryptFileWork(password, inputFile, outputFile);

        AsyncFileEncryption asyncFileEncryption = new AsyncFileEncryption();
        asyncFileEncryption.setLiveData(encryptionActionLiveData);
        asyncFileEncryption.execute(encryptFileWork);

        return asyncFileEncryption;
    }

    public AsyncFileDecryption decryptFile(String password, FileListItem fileListItem) {
        File inputFile = new File(fileListItem.getLocation());
        // Remove the .jei file extension
        String outputFilePath = fileListItem.getLocation().replace(".jei", "");
        File outputFile = new File(outputFilePath);
        EncryptFileWork encryptFileWork = new EncryptFileWork(password, inputFile, outputFile);

        AsyncFileDecryption asyncFileDecryption = new AsyncFileDecryption();
        asyncFileDecryption.execute(encryptFileWork);

        return asyncFileDecryption;

    }

    private boolean checkAndCreateAppDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        if (!directory.exists()) {
            return directory.mkdir();
        }

        return true;
    }

    private void updateFiles() {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        if (!directory.exists() || directory.listFiles() == null) return;

        encryptedFilesList.clear();
        decryptedFilesList.clear();

        for (File file : directory.listFiles()) {
            FileListItem fileListItem = new FileListItem();
            fileListItem.setLocation(file.getPath());
            fileListItem.setTime(file.lastModified());
            fileListItem.setFilename(file.getName());
            fileListItem.setSize(file.length());

            // Need to check the file extensions to include only .jei files
            if (file.getName().endsWith(".jei")) {
                // Add the file to the encrypted set
                encryptedFilesList.add(fileListItem);
            } else {
                // Add the file to the decrypted set
                decryptedFilesList.add(fileListItem);
            }
        }

        encryptedFilesLiveData.postValue(encryptedFilesList);
        decryptedFilesLiveData.postValue(decryptedFilesList);
    }

    public void populateFiles() {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        if (!directory.exists() || directory.listFiles() == null) return;

        encryptedFilesList.clear();
        decryptedFilesList.clear();

        for (File file : directory.listFiles()) {
            FileListItem fileListItem = new FileListItem();
            fileListItem.setLocation(file.getPath());
            fileListItem.setTime(file.lastModified());
            fileListItem.setFilename(file.getName());
            fileListItem.setSize(file.length());

            // Need to check the file extensions to include only .jei files
            if (file.getName().endsWith(".jei")) {
                // Add the file to the encrypted set
                encryptedFilesList.add(fileListItem);
            } else {
                // Add the file to the decrypted set
                decryptedFilesList.add(fileListItem);
            }
        }

        encryptedFilesLiveData.postValue(encryptedFilesList);
        decryptedFilesLiveData.postValue(decryptedFilesList);
    }

    public SingleLiveEvent<EncryptionFileResult> getEncryptionLiveData() {
        return encryptionLiveData;
    }

    public MutableLiveData<List<FileListItem>> getEncryptedFilesLiveData() {
        return encryptedFilesLiveData;
    }

    public MutableLiveData<List<FileListItem>> getDecryptedFilesLiveData() {
        return decryptedFilesLiveData;
    }

    public List<FileListItem> getEncryptedFilesList() {
        return encryptedFilesList;
    }

    public List<FileListItem> getDecryptedFilesList() {
        return decryptedFilesList;
    }

    public FileObserver getFileObserver() {
        return fileObserver;
    }

    public SingleLiveEvent<EncryptionFileResult> getDecryptedLiveData() {
        return decryptedLiveData;
    }

    public SingleLiveEvent<EncryptionFileResult> getEncryptionActionLiveData() {
        return encryptionActionLiveData;
    }

    public MutableLiveData<Integer> getCryptoProgressLiveData() {
        return cryptoProgressLiveData;
    }

    public class AsyncFileEncryption extends AsyncTask<EncryptFileWork, Integer, EncryptionFileResult> {

        private SingleLiveEvent<EncryptionFileResult> liveData;
        private File outputFile;

        @Override
        protected EncryptionFileResult doInBackground(EncryptFileWork... params) {
            backgroundFileWork.set(true);

            EncryptFileWork encryptFileWork = params[0];

            outputFile = encryptFileWork.getOutputFile();

            EncryptionFileResult encryptionFileResult = new EncryptionFileResult();

            try {
                Key secretKey = new SecretKeySpec(encryptFileWork.getPassword().getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);

                FileInputStream inputStream = new FileInputStream(encryptFileWork.getInputFile());
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                byte[] input = new byte[2048];
                int bytesRead;

                long totalFileSize = encryptFileWork.getInputFile().length();
                double percentUnit = 100.0 / totalFileSize;
                long readLength = 0;

                while ((bytesRead = inputStream.read(input)) != -1) {
                    if (isCancelled()) break;

                    byte[] output = cipher.update(input, 0, bytesRead);
                    readLength += bytesRead;
                    cryptoProgressLiveData.postValue((int) Math.round(percentUnit * readLength));

                    if (output != null) {
                        outputStream.write(output);
                    }
                }

                if (!isCancelled()) {
                    byte[] output = cipher.doFinal();
                    if (output != null)
                        outputStream.write(output);

                    cryptoProgressLiveData.postValue(100);
                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();

            } catch (NoSuchPaddingException | NoSuchAlgorithmException
                    | InvalidKeyException | BadPaddingException
                    | IllegalBlockSizeException | IOException ex) {
                CryptoException cryptoException = new CryptoException("Error encrypting/decrypting file", ex);
                encryptionFileResult.setError(cryptoException);
            }

            return encryptionFileResult;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(EncryptionFileResult encryptionFileResult) {
            backgroundFileWork.set(false);
            populateFiles();
            liveData.postValue(encryptionFileResult);
        }

        @Override
        protected void onCancelled(EncryptionFileResult result) {
            super.onCancelled(result);

            if (outputFile != null) {
                outputFile.delete();
            }
        }

        public void setLiveData(SingleLiveEvent<EncryptionFileResult> liveData) {
            this.liveData = liveData;
        }
    }

    public class AsyncFileDecryption extends AsyncTask<EncryptFileWork, Void, EncryptionFileResult> {

        private File outputFile;

        @Override
        protected EncryptionFileResult doInBackground(EncryptFileWork... params) {
            backgroundFileWork.set(true);

            EncryptFileWork encryptFileWork = params[0];

            outputFile = encryptFileWork.getOutputFile();

            EncryptionFileResult encryptionFileResult = new EncryptionFileResult();
            encryptionFileResult.setInputFile(encryptFileWork.getInputFile());
            encryptionFileResult.setOutputFile(encryptFileWork.getOutputFile());

            try {
                Key secretKey = new SecretKeySpec(encryptFileWork.getPassword().getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);

                FileInputStream inputStream = new FileInputStream(encryptFileWork.getInputFile());
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                byte[] input = new byte[2048];
                int bytesRead;

                long totalFileSize = encryptFileWork.getInputFile().length();
                double percentUnit = 100.0 / totalFileSize;
                long readLength = 0;

                while ((bytesRead = inputStream.read(input)) != -1) {
                    if (isCancelled()) break;

                    byte[] output = cipher.update(input, 0, bytesRead);
                    readLength += bytesRead;
                    cryptoProgressLiveData.postValue((int) Math.round(percentUnit * readLength));

                    if (output != null) {
                        outputStream.write(output);
                    }
                }

                if (!isCancelled()) {
                    byte[] output = cipher.doFinal();
                    if (output != null)
                        outputStream.write(output);

                    cryptoProgressLiveData.postValue(100);
                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();

            } catch (NoSuchPaddingException | NoSuchAlgorithmException
                    | InvalidKeyException | BadPaddingException
                    | IllegalBlockSizeException | IOException ex) {
                CryptoException cryptoException = new CryptoException("Error encrypting/decrypting file", ex);
                encryptionFileResult.setError(cryptoException);
            }

            return encryptionFileResult;
        }

        @Override
        protected void onPostExecute(EncryptionFileResult encryptionFileResult) {
            backgroundFileWork.set(false);
            populateFiles();
            decryptedLiveData.postValue(encryptionFileResult);
        }

        @Override
        protected void onCancelled(EncryptionFileResult result) {
            super.onCancelled(result);

            if (outputFile != null) {
                outputFile.delete();
            }
        }
    }
}
