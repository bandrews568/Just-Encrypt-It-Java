package github.bandrews568.justencryptit;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;


public class JustEncryptItApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Directory where all the encrypted and decrypted files will be stored
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Toast.makeText(this, "\"Error making /JustEncryptIt directory\"", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
