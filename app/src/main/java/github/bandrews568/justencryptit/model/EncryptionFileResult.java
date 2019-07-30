package github.bandrews568.justencryptit.model;

import androidx.annotation.Nullable;

public class EncryptionFileResult {

    @Nullable
    private Exception error;

    public void setError(@Nullable Exception error) {
        this.error = error;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
