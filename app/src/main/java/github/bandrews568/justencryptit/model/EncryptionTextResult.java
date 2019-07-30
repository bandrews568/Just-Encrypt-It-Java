package github.bandrews568.justencryptit.model;

import androidx.annotation.Nullable;

public class EncryptionTextResult {

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
