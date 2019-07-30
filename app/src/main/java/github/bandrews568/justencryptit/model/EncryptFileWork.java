package github.bandrews568.justencryptit.model;

import java.io.File;

/**
 * Used when performing file encryption background work with AsyncTask
 */
public class EncryptFileWork {

    private final String password;
    private final File inputFile;
    private final File outputFile;

    public EncryptFileWork(String password, File inputFile, File outputFile) {
        this.password = password;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public String getPassword() {
        return password;
    }

    public File getInputFile() {
        return inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }
}
