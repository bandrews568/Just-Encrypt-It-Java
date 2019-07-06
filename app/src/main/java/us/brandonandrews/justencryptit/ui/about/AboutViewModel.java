package us.brandonandrews.justencryptit.ui.about;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {

    public Uri justEncryptItUri() {
        String justEncryptItUrl = "https://play.google.com/store/apps/details?id=us.brandonandrews.justencryptit&hl=en_US";
        return Uri.parse(justEncryptItUrl);
    }

    public Uri authorUri() {
        String authorUrl = "https://github.com/bandrews568";
        return Uri.parse(authorUrl);
    }

    public Uri sourceCodeUri() {
        String sourceCodeUrl = "https://github.com/bandrews568/Just-Encrypt-It-Java";
        return Uri.parse(sourceCodeUrl);
    }
}




