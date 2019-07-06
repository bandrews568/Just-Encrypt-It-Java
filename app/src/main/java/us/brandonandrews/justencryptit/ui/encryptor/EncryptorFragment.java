package us.brandonandrews.justencryptit.ui.encryptor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import us.brandonandrews.justencryptit.R;
import us.brandonandrews.justencryptit.ui.ZoomDialog;

import static us.brandonandrews.justencryptit.ui.settings.SettingsActivity.CUSTOM_PASSWORD_MAX;
import static us.brandonandrews.justencryptit.ui.settings.SettingsActivity.customPassword;
import static us.brandonandrews.justencryptit.ui.settings.SettingsActivity.customPasswordNames;

public class EncryptorFragment extends Fragment {

    @BindView(R.id.tilPassword) TextInputLayout tilPassword;
    @BindView(R.id.etEnterText) EditText etEnterText;
    @BindView(R.id.tvFinalText) TextView tvFinalText;

    @BindView(R.id.btnEncrypt) Button btnEncrypt;
    @BindView(R.id.btnDecrypt) Button btnDecrypt;
    @BindView(R.id.btnClear) Button btnClear;
    @BindView(R.id.btnCopy) Button btnCopy;
    @BindView(R.id.btnPaste) Button btnPaste;
    @BindView(R.id.btnZoom) ImageButton btnZoom;

    @BindView(R.id.cbSavePassword) CheckBox cbSavePassword;
    @BindView(R.id.fab) FloatingActionButton fab;

    @BindView(R.id.spinnerCustomPasswords) Spinner spinnerCustomPassword;

    // Butterknife
    private Unbinder unbinder;

    private EncryptorViewModel viewModel;

    private SharedPreferences sharedPreferences;
    private InputMethodManager keyboardManager;
    private ClipboardManager clipboard;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encryptor, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(EncryptorViewModel.class);
        viewModel.getEncryptionLiveData().observe(this, encryptionResult -> {
            if (encryptionResult.getError() != null) {
                if (encryptionResult.getError() instanceof EncryptionInitializationException) {
                    tilPassword.setError("Invalid password");
                } else if (encryptionResult.getError() instanceof EncryptionOperationNotPossibleException) {
                    makeToast("Can't decrypt text");
                } else if (encryptionResult.getError() instanceof IllegalArgumentException) {
                    tilPassword.setError("Required");
                }
            } else {
                tvFinalText.setText(encryptionResult.getText());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        keyboardManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        sharedPreferences = getActivity().getSharedPreferences("password", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final String getPassword = sharedPreferences.getString("password", "none");
        boolean savePasswordChecked = sharedPreferences.getBoolean("checked", false);

        ArrayList<String> spinnerOptions = new ArrayList<>();
        spinnerOptions.add("Saved Passwords");
        spinnerCustomPassword.setVisibility(View.INVISIBLE);

        // Check to see if user has checked that they wanted to remember a password.
        // If so, populate etPassword with that password.
        if (savePasswordChecked && !getPassword.equals("none")) {
            tilPassword.getEditText().setText(getPassword);
            cbSavePassword.setChecked(true);
        }

        // Spinner adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerOptions) {
            // Used to disable `Saved Passwords`
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };

        setSpinnerOptions(sharedPreferences, spinnerOptions, spinnerCustomPassword);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomPassword.setAdapter(spinnerAdapter);

        // Save password checkbox
        cbSavePassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cbSavePassword.isChecked()){
                editor.putString("password", tilPassword.getEditText().getText().toString());
                editor.putBoolean("checked", true);
            } else {
                editor.putBoolean("checked", false);
            }
            editor.commit();
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("password", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        CheckBox cbSavePassword = getView().findViewById(R.id.cbSavePassword);

        if (cbSavePassword.isChecked()) {
            editor.putString("password", tilPassword.getEditText().getText().toString());
            editor.commit();
        }
    }

    @OnItemSelected(R.id.spinnerCustomPasswords)
    public void customPasswordSelected(int position) {
        if (position > 0) {
            String setPasswordEditText = sharedPreferences.getString(customPassword[position - 1], "");
            tilPassword.getEditText().setText(setPasswordEditText);
        }
    }

    @OnClick(R.id.fab)
    public void fabClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, tvFinalText.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @OnClick(R.id.btnEncrypt)
    public void encryptClicked(View v) {
        String password = tilPassword.getEditText().getText().toString();
        String text = etEnterText.getText().toString();

        tilPassword.setError(null);
        viewModel.encryptText(text, password);

        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.btnDecrypt)
    public void decryptClicked(View v) {
        String password = tilPassword.getEditText().getText().toString();
        String text = etEnterText.getText().toString();

        tilPassword.setError(null);
        viewModel.decryptText(text, password);

        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.btnCopy)
    public void copyClicked() {
        String msg = "Text copied to clipboard";
        ClipData text = ClipData.newPlainText("text", tvFinalText.getText());

        if (clipboard != null) {
            clipboard.setPrimaryClip(text);
            makeToast(msg);
        }
    }

    @OnClick(R.id.btnPaste)
    public void pasteClicked() {
        try {

            if (clipboard != null) {
                String msg = "Text pasted from clipboard";
                ClipData clip = clipboard.getPrimaryClip();

                if (clip != null) {
                    ClipData.Item item = clip.getItemAt(0);
                    String text = item.getText().toString();
                    etEnterText.setText(text);
                    makeToast(msg);
                }

            }

        } catch (NullPointerException e) {
            // Nothing in clipboard to paste
            String msg = "Nothing to paste";
            makeToast(msg);
        }
    }

    @OnClick(R.id.btnClear)
    public void clearClicked(View v) {
        final String tempEnterText = etEnterText.getText().toString();
        final String tempFinalText = tvFinalText.getText().toString();

        etEnterText.setText("");
        tvFinalText.setText("");

        Snackbar snackbar = Snackbar
                .make(v, "Text cleared", Snackbar.LENGTH_LONG)
                .setAction("UNDO", _v -> {
                    etEnterText.setText(tempEnterText);
                    tvFinalText.setText(tempFinalText);
                    Snackbar snackbarUndo = Snackbar.make(getView(),
                            "Text restored", Snackbar.LENGTH_SHORT);
                    snackbarUndo.show();
                });
        snackbar.show();
    }

    @OnClick(R.id.btnZoom)
    public void zoomClicked() {
        if (!tvFinalText.getText().toString().equals("")) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ZoomDialog editNameDialogFragment =
                    ZoomDialog.newInstance(tvFinalText.getText().toString());
            editNameDialogFragment.show(fm, "fragment_edit_name");
        } else {
            makeToast("No text to zoom");
        }
    }

    public void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void setSpinnerOptions(SharedPreferences prefs,
                                  ArrayList<String> options, Spinner spinnerWidget) {
        for (int i = 0; i < CUSTOM_PASSWORD_MAX; i++) {
            String getCustomPasswordName = prefs.getString(customPasswordNames[i], "");
            String getCustomPassword = prefs.getString(customPassword[i], "");

            if (!getCustomPasswordName.equals("") && !getCustomPassword.equals("")) {
                options.add(getCustomPasswordName);
                spinnerWidget.setVisibility(View.VISIBLE);
            }
        }
    }
}
