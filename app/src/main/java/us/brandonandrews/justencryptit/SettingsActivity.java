package us.brandonandrews.justencryptit;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


public class SettingsActivity extends AppCompatActivity {

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

        EditText etCustomPasswordName1 = (EditText) findViewById(R.id.etCustomPasswordName1);
        EditText etCustomPasswordName2 = (EditText) findViewById(R.id.etCustomPasswordName2);
        EditText etCustomPasswordName3 = (EditText) findViewById(R.id.etCustomPasswordName3);

        EditText etCustomPassword1 = (EditText) findViewById(R.id.etCustomPassword1);
        EditText etCustomPassword2 = (EditText) findViewById(R.id.etCustomPassword2);
        EditText etCustomPassword3 = (EditText) findViewById(R.id.etCustomPassword3);

        customPasswordNameEditText = new EditText[] {etCustomPasswordName1,
                                                     etCustomPasswordName2,
                                                     etCustomPasswordName3};
        customPasswordEditText = new EditText[] {etCustomPassword1,
                                                 etCustomPassword2,
                                                 etCustomPassword3};

        prefs = getSharedPreferences("password", Context.MODE_PRIVATE);

        for (int i = 0; i < 3; i++) {
            String getCustomPasswordName = prefs.getString(customPasswordNames[i], "");
            String getCustomPassword = prefs.getString(customPassword[i], "");
            customPasswordNameEditText[i].setText(getCustomPasswordName);
            customPasswordEditText[i].setText(getCustomPassword);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setPasswordSettings();
    }

    private void setPasswordSettings() {
        SharedPreferences.Editor editor = prefs.edit();

        for (int i = 0; i < 3; i++) {
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
