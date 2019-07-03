package us.brandonandrews.justencryptit.ui.encryptor;

import androidx.annotation.Nullable;

class EncryptionResult {

    private String text;
    @Nullable
    private Exception error;

    public void setText(String text) {
        this.text = text;
    }

    public void setError(@Nullable Exception error) {
        this.error = error;
    }

    public String getText() {
        return text;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
