package us.brandonandrews.justencryptit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         final ClipboardManager clipboard = (ClipboardManager)
                 getSystemService(Context.CLIPBOARD_SERVICE);

        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etEnterText = (EditText) findViewById(R.id.etEnterText);
        final TextView tvFinalText = (TextView) findViewById(R.id.tvFinalText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, tvFinalText.getText());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        SharedPreferences sharedpreferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        final CheckBox cbSavePassword = (CheckBox) findViewById(R.id.cbSavePassword);

        final SharedPreferences.Editor editor = sharedpreferences.edit();
        final String getPassword = sharedpreferences.getString("password", "none");
        boolean savePasswordChecked = sharedpreferences.getBoolean("checked", false);

        if (savePasswordChecked && !getPassword.equals("none")) {
            etPassword.setText(getPassword);
            cbSavePassword.setChecked(true);
        }

        cbSavePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbSavePassword.isChecked()){
                    editor.putString("password", etPassword.getText().toString());
                    editor.putBoolean("checked", true);
                } else {
                    editor.putBoolean("checked", false);
                }
                editor.commit();
            }
        });

        final Button btnMakeText = (Button) findViewById(R.id.btnMakeText);
        btnMakeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finalText;
                String msg = "Please enter a password";
                String invalidTextMsg = "Invalid text to decrypt";

                String btnText = (String) btnMakeText.getText();
                String password = etPassword.getText().toString();
                String text = etEnterText.getText().toString();

                try {
                    switch (btnText) {
                        case "Encrypt":
                            finalText = Encryption.encrypt(password, text);
                            tvFinalText.setText(finalText);
                            break;
                        case "Decrypt":
                            finalText = Encryption.decrypt(password, text);
                            tvFinalText.setText(finalText);
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    makeToast(msg);
                } catch (EncryptionOperationNotPossibleException e) {
                    makeToast(invalidTextMsg);
                }

            }
        });

        // Check to see if user has selected `encrypt` or `decrypt`
        RadioGroup rbEncryptOrDecrypt = (RadioGroup) findViewById(R.id.rgEncryptOrDecrypt);
        rbEncryptOrDecrypt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    switch (rb.getId()) {
                        case R.id.rbEncrypt:
                            String textEncrypt = "Enter text to be encrypted";
                            etEnterText.setHint(textEncrypt);
                            btnMakeText.setText("Encrypt");
                            break;
                        case R.id.rbDecrypt:
                            String textDecrypt = "Enter text to be decrypted";
                            etEnterText.setHint(textDecrypt);
                            btnMakeText.setText("Decrypt");
                            break;
                    }
                }
            }
        });

        // Copy button
        Button btnCopy = (Button) findViewById(R.id.btnCopy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Text copied to clipboard";
                ClipData text = ClipData.newPlainText("text", tvFinalText.getText());
                clipboard.setPrimaryClip(text);
                makeToast(msg);
            }
        });

        // Paste button
        Button btnPaste = (Button) findViewById(R.id.btnPaste);
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Text pasted from clipboard";
                ClipData clip = clipboard.getPrimaryClip();
                ClipData.Item item = clip.getItemAt(0);
                String text = item.getText().toString();
                etEnterText.setText(text);
                makeToast(msg);
            }
        });

        // Clear button
        Button btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEnterText.setText("");
                String textMsg = "Encrypted or decrypted text";
                tvFinalText.setText(textMsg);
                String msg = "Text cleared";
                makeToast(msg);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Clean this up later!
        SharedPreferences sharedpreferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        CheckBox cbSavePassword = (CheckBox) findViewById(R.id.cbSavePassword);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        if (cbSavePassword.isChecked()) {
            editor.putString("password", etPassword.getText().toString());
            editor.commit();
        }
    }

    public void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}