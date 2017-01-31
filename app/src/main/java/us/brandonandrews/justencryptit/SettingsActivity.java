package us.brandonandrews.justencryptit;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPrefs = getSharedPreferences("password", Context.MODE_PRIVATE);

        EditText etCustomPasswordName1 = (EditText) findViewById(R.id.etCustomPasswordName1);
        EditText etCustomPasswordName2 = (EditText) findViewById(R.id.etCustomPasswordName2);
        EditText etCustomPasswordName3 = (EditText) findViewById(R.id.etCustomPasswordName3);

        EditText etCustomPassword1 = (EditText) findViewById(R.id.etCustomPassword1);
        EditText etCustomPassword2 = (EditText) findViewById(R.id.etCustomPassword2);
        EditText etCustomPassword3 = (EditText) findViewById(R.id.etCustomPassword3);

        String getCustomPasswordName1 = mPrefs.getString("customPasswordName1", "");
        String getCustomPasswordName2 = mPrefs.getString("customPasswordName2", "");
        String getCustomPasswordName3 = mPrefs.getString("customPasswordName3", "");

        String getCustomPassword1 = mPrefs.getString("customPassword1", "");
        String getCustomPassword2 = mPrefs.getString("customPassword2", "");
        String getCustomPassword3 = mPrefs.getString("customPassword3", "");

        etCustomPasswordName1.setText(getCustomPasswordName1);
        etCustomPasswordName2.setText(getCustomPasswordName2);
        etCustomPasswordName3.setText(getCustomPasswordName3);

        etCustomPassword1.setText(getCustomPassword1);
        etCustomPassword2.setText(getCustomPassword2);
        etCustomPassword3.setText(getCustomPassword3);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditior = mPrefs.edit();

        EditText etCustomPasswordName1 = (EditText) findViewById(R.id.etCustomPasswordName1);
        EditText etCustomPasswordName2 = (EditText) findViewById(R.id.etCustomPasswordName2);
        EditText etCustomPasswordName3 = (EditText) findViewById(R.id.etCustomPasswordName3);

        EditText etCustomPassword1 = (EditText) findViewById(R.id.etCustomPassword1);
        EditText etCustomPassword2 = (EditText) findViewById(R.id.etCustomPassword2);
        EditText etCustomPassword3 = (EditText) findViewById(R.id.etCustomPassword3);

        mEditior.putString("customPasswordName1", etCustomPasswordName1.getText().toString());
        mEditior.putString("customPasswordName2", etCustomPasswordName2.getText().toString());
        mEditior.putString("customPasswordName3", etCustomPasswordName3.getText().toString());

        mEditior.putString("customPassword1", etCustomPassword1.getText().toString());
        mEditior.putString("customPassword2", etCustomPassword2.getText().toString());
        mEditior.putString("customPassword3", etCustomPassword3.getText().toString());

        mEditior.commit();
    }
}
