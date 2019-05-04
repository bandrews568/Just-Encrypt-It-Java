package us.brandonandrews.justencryptit;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


public class SettingsActivity extends AppCompatActivity {

    public static final int CUSTOM_PASSWORD_MAX = 3;
    private SharedPreferences prefs;
    public static EditText[] customPasswordNameEditText;
    public static EditText[] customPasswordEditText;
    public static String[] customPasswordNames = new String[] {"customPasswordName1",
                                                               "customPasswordName2",
                                                               "customPasswordName3"};
    public static String[] customPassword = new String[] {"customPassword1",
                                                         "customPassword2",
                                                         "customPassword3"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText etCustomPasswordName1 = findViewById(R.id.etCustomPasswordName1);
        EditText etCustomPasswordName2 = findViewById(R.id.etCustomPasswordName2);
        EditText etCustomPasswordName3 = findViewById(R.id.etCustomPasswordName3);

        EditText etCustomPassword1 = findViewById(R.id.etCustomPassword1);
        EditText etCustomPassword2 = findViewById(R.id.etCustomPassword2);
        EditText etCustomPassword3 = findViewById(R.id.etCustomPassword3);

        customPasswordNameEditText = new EditText[] {etCustomPasswordName1,
                                                     etCustomPasswordName2,
                                                     etCustomPasswordName3};
        customPasswordEditText = new EditText[] {etCustomPassword1,
                                                 etCustomPassword2,
                                                 etCustomPassword3};

        prefs = getSharedPreferences("password", Context.MODE_PRIVATE);
        getPasswordSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setPasswordSettings();
    }

    private void getPasswordSettings() {
        for (int i = 0; i < CUSTOM_PASSWORD_MAX; i++) {
            String getCustomPasswordName = prefs.getString(customPasswordNames[i], "");
            String getCustomPassword = prefs.getString(customPassword[i], "");
            customPasswordNameEditText[i].setText(getCustomPasswordName);
            customPasswordEditText[i].setText(getCustomPassword);
        }
    }

    private void setPasswordSettings() {
        SharedPreferences.Editor editor = prefs.edit();

        for (int i = 0; i < CUSTOM_PASSWORD_MAX; i++) {
            String currentCustomPasswordName = customPasswordNames[i];
            String currentCustomPassword = customPassword[i];
            EditText currentCustomPasswordNameEt = customPasswordNameEditText[i];
            EditText currentCustomPasswordEt = customPasswordEditText[i];
            String currentCustomPasswordNameText = currentCustomPasswordNameEt.getText().toString();
            String currentCustomPasswordText = currentCustomPasswordEt.getText().toString();

            editor.putString(currentCustomPasswordName, currentCustomPasswordNameText);
            editor.putString(currentCustomPassword, currentCustomPasswordText);
        }
        editor.commit();
    }
}
