package github.bandrews568.justencryptit.model;

import androidx.annotation.Nullable;

import java.io.File;

public class EncryptionFileResult {

    private File inputFile;
    private File outputFile;

    @Nullable
    private Exception error;

    public void setError(@Nullable Exception error) {
        this.error = error;
    }

    @Nullable
    public Exception getError() {
        return error;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
}
