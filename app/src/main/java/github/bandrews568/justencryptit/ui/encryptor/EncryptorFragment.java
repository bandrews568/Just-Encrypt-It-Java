package github.bandrews568.justencryptit.ui.encryptor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;

public class EncryptorFragment extends Fragment {

    @BindView(R.id.tilPassword) TextInputLayout tilPassword;
    @BindView(R.id.etEnterText) EditText etEnterText;

    @BindView(R.id.btnEncrypt) Button btnEncrypt;
    @BindView(R.id.btnDecrypt) Button btnDecrypt;
    @BindView(R.id.btnPaste) ImageButton btnPaste;
    @BindView(R.id.btnCopy) ImageButton btnCopy;
    @BindView(R.id.btnShare) ImageButton btnShare;

    @BindView(R.id.cbSavePassword) CheckBox cbSavePassword;

    // Butterknife
    private Unbinder unbinder;

    private EncryptorViewModel viewModel;

    private SharedPreferences sharedPreferences;
    private InputMethodManager keyboardManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
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
                encryptionResult.getError().printStackTrace();
                if (encryptionResult.getError() instanceof EncryptionInitializationException) {
                    tilPassword.setError("Invalid password");
                } else if (encryptionResult.getError() instanceof EncryptionOperationNotPossibleException) {
                    etEnterText.setError("Can't decrypt text");
                } else if (encryptionResult.getError() instanceof IllegalArgumentException) {
                    tilPassword.setError("Required");
                }
            } else {
                boolean showInDialog = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("show_in_dialog", false);

                if (showInDialog) {
                    DialogFragment resultDialog = ResultDialog.newInstance(encryptionResult.getText());
                    resultDialog.show(getFragmentManager(), "result_dialog");
                } else {
                    etEnterText.setText(encryptionResult.getText());
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        keyboardManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        sharedPreferences = getActivity().getSharedPreferences("password", Context.MODE_PRIVATE);

        String getPassword = sharedPreferences.getString("password", "none");
        boolean savePasswordChecked = sharedPreferences.getBoolean("checked", false);

        // Check to see if user has checked that they wanted to remember a password.
        // If so, populate etPassword with that password.
        if (savePasswordChecked && !getPassword.equals("none")) {
            tilPassword.getEditText().setText(getPassword);
            cbSavePassword.setChecked(true);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            tilPassword.getEditText().setText(savedInstanceState.getString("password", ""));
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        String encryptionType = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("algorithm_choice", "");

        if (encryptionType.equals("AES")) {
            tilPassword.setCounterEnabled(true);
            tilPassword.setCounterMaxLength(16);
        } else {
            tilPassword.setCounterEnabled(false);
        }

        boolean showInDialog = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("show_in_dialog", false);

        btnCopy.setVisibility(showInDialog ? View.GONE : View.VISIBLE);
        btnShare.setVisibility(showInDialog ? View.GONE : View.VISIBLE);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("password", tilPassword.getEditText().getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnEncrypt)
    public void encryptClicked(View v) {
        String password = tilPassword.getEditText().getText().toString();
        String text = etEnterText.getText().toString();

        if (!TextUtils.isEmpty(text)) {
            tilPassword.setError(null);
            String algorithm = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getString("algorithm_choice", "Basic");

            viewModel.encryptText(text, password, algorithm);

            if (keyboardManager != null) {
                keyboardManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    @OnClick(R.id.btnDecrypt)
    public void decryptClicked(View v) {
        String password = tilPassword.getEditText().getText().toString();
        String text = etEnterText.getText().toString();

        if (!TextUtils.isEmpty(text)) {
            tilPassword.setError(null);

            String algorithm = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getString("algorithm_choice", "Basic");

            viewModel.decryptText(text, password, algorithm);

            if (keyboardManager != null) {
                keyboardManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    @OnClick(R.id.btnPaste)
    public void pasteClicked() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null) {
            ClipData clip = clipboard.getPrimaryClip();

            if (clip != null) {
                ClipData.Item item = clip.getItemAt(0);
                CharSequence text = item.getText();

                if (text != null) {
                    if (text.length() > 30000) {
                        CharSequence truncatedText = text.subSequence(0, 30000);
                        String toastMessage = String.format("Couldn't add %d characters", text.length() - 30000);
                        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
                        etEnterText.setText(truncatedText);
                    } else {
                        etEnterText.setText(text);
                    }
                }
            }
        }
    }

    @OnClick(R.id.btnClear)
    public void clearClicked(View v) {
        String tempEnterText = etEnterText.getText().toString();

        etEnterText.setText("");

        Snackbar snackbar = Snackbar
                .make(v, "Text cleared", Snackbar.LENGTH_LONG)
                .setAction("UNDO", _v -> etEnterText.setText(tempEnterText));
        snackbar.show();
    }

    @OnClick(R.id.btnCopy)
    public void copyClicked() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null) {
            ClipData text = ClipData.newPlainText("text", etEnterText.getText());
            clipboard.setPrimaryClip(text);
        }
    }

    @OnClick(R.id.btnShare)
    public void shareClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, etEnterText.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @OnCheckedChanged(R.id.cbSavePassword)
    public void savePasswordCheckChange(CompoundButton button, boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isChecked){
            editor.putString("password", tilPassword.getEditText().getText().toString());
            editor.putBoolean("checked", true);
        } else {
            editor.putBoolean("checked", false);
        }
        editor.commit();
    }
}
