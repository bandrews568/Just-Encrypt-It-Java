package us.brandonandrews.justencryptit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        final CheckBox cbSavePassword = (CheckBox) findViewById(R.id.cbSavePassword);
        final Button btnMakeText = (Button) findViewById(R.id.btnMakeText);
        Button btnClear = (Button) findViewById(R.id.btnClear);
        Button btnCopy = (Button) findViewById(R.id.btnCopy);
        Button btnPaste = (Button) findViewById(R.id.btnPaste);
        RadioGroup rbEncryptOrDecrypt = (RadioGroup) findViewById(R.id.rgEncryptOrDecrypt);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        SharedPreferences sharedpreferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        final String getPassword = sharedpreferences.getString("password", "none");
        boolean savePasswordChecked = sharedpreferences.getBoolean("checked", false);

        if (savePasswordChecked && !getPassword.equals("none")) {
            etPassword.setText(getPassword);
            cbSavePassword.setChecked(true);
        }


        // Fab button
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

        // Save password checkbox
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

        // Button to encrypt or decrypt the text
        // Delegates the actual task to AsyncCreateText on a separate thread
        btnMakeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = (String) btnMakeText.getText();
                String password = etPassword.getText().toString();
                String text = etEnterText.getText().toString();

                switch (btnText) {
                    case "Encrypt":
                        new AsyncCreateText().execute(password, text, "encrypt");
                        break;
                    case "Decrypt":
                        new AsyncCreateText().execute(password, text, "decrypt");
                        break;
                }
            }
        });

        // Check to see if user has selected `encrypt` or `decrypt`
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
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String msg = "Text pasted from clipboard";
                    ClipData clip = clipboard.getPrimaryClip();
                    ClipData.Item item = clip.getItemAt(0);
                    String text = item.getText().toString();
                    etEnterText.setText(text);
                    makeToast(msg);
                } catch (NullPointerException e) {
                    // Nothing in clipboard to paste
                    String msg = "Nothing to paste";
                    makeToast(msg);
                }

            }
        });

        // Clear button
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tempEnterText = etEnterText.getText().toString();
                final String tempFinalText = tvFinalText.getText().toString();

                etEnterText.setText("");
                tvFinalText.setText("");

                Snackbar snackbar = Snackbar
                        .make(v, "Text cleared", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                etEnterText.setText(tempEnterText);
                                tvFinalText.setText(tempFinalText);
                                Snackbar snackbarUndo = Snackbar.make(view,
                                        "Text restored", Snackbar.LENGTH_SHORT);
                                snackbarUndo.show();
                            }
                        });
                snackbar.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedpreferences = getSharedPreferences("password", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        CheckBox cbSavePassword = (CheckBox) findViewById(R.id.cbSavePassword);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        if (cbSavePassword.isChecked()) {
            editor.putString("password", etPassword.getText().toString());
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: custom settings
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: custom settings
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class AsyncCreateText extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String finalText;
            String password = params[0];
            String text = params[1];
            String type = params[2];

            try {
                if (type.equals("encrypt")) {
                    finalText = Encryption.encrypt(password, text);
                } else if (type.equals("decrypt")){
                    finalText = Encryption.decrypt(password, text);
                } else {
                    finalText = "";
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return "IllegalArgument";
            } catch (EncryptionOperationNotPossibleException e) {
                e.printStackTrace();
                return "Can't decrypt";
            }
            return finalText;
        }

        @Override
        protected void onPostExecute(String text) {
            String invalidPassword = "Please enter a password";
            String invalidTextMsg = "Invalid text to decrypt";

            if (text.equals("IllegalArgument")) {
                makeToast(invalidPassword);
            } else if (text.equals("Can't decrypt")) {
                makeToast(invalidTextMsg);
            } else {
                TextView tvFinalText = (TextView) findViewById(R.id.tvFinalText);
                tvFinalText.setText(text);
            }
        }
    }
}