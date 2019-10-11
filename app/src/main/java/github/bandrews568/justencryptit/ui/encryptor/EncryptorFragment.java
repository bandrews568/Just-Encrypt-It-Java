package github.bandrews568.justencryptit.ui.encryptor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import javax.crypto.BadPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.utils.UiUtils;

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
    private Snackbar snackbar;
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

        btnDecrypt.setEnabled(false);
        btnDecrypt.setAlpha(0.7f);
        btnEncrypt.setEnabled(false);
        btnEncrypt.setAlpha(0.7f);

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
                } else if (encryptionResult.getError() instanceof EncryptionOperationNotPossibleException
                        || encryptionResult.getError() instanceof IllegalArgumentException) {
                    UiUtils.errorToast(requireContext(), "Can't decrypt text");
                } else if (encryptionResult.getError() instanceof BadPaddingException) {
                    tilPassword.setError("Invalid password");
                }
            } else if (encryptionResult.getText().length() > 30000) {
                UiUtils.errorToast(requireContext(), "Over 30,000 character limit");
            } else {
                boolean showInDialog = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("show_in_dialog", false);

                if (showInDialog) {
                    DialogFragment resultDialog = ResultDialog.newInstance(encryptionResult.getText());
                    resultDialog.show(getFragmentManager(), "result_dialog");
                } else {
                    etEnterText.setError(null);
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
    public void onStop() {
        super.onStop();

        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("password", tilPassword.getEditText().getText().toString());
        outState.putString("text", etEnterText.getText().toString());
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

        String algorithm = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString("algorithm_choice", "Basic");

        if (password.length() == 0) {
            tilPassword.setError("Required");
            return;
        }

        if (algorithm.equals("AES") && password.length() != 16) {
            tilPassword.setError("Must be 16 characters");
            return;
        }

        if (!TextUtils.isEmpty(text)) {
            tilPassword.setError(null);


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

        String algorithm = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString("algorithm_choice", "Basic");

        if (password.length() == 0) {
            tilPassword.setError("Required");
            return;
        }

        if (algorithm.equals("AES") && password.length() != 16) {
            tilPassword.setError("Must be 16 characters");
            return;
        }


        if (!TextUtils.isEmpty(text)) {
            tilPassword.setError(null);

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
                        UiUtils.errorToast(requireContext(), String.format("Couldn't add %d characters", text.length() - 30000));
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

        snackbar = Snackbar
                .make(v, "Text cleared", Snackbar.LENGTH_LONG)
                .setAction("UNDO", _v -> etEnterText.setText(tempEnterText));

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                snackbar.getView().getLayoutParams();
        params.setAnchorId(R.id.main_bottom_navigation); //id of the bottom navigation view
        params.gravity = Gravity.TOP;
        params.anchorGravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);

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

    @OnTextChanged(value=R.id.etEnterText, callback=OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChanged(CharSequence s) {
        if (s.toString().trim().length() == 0) {
            btnDecrypt.setEnabled(false);
            btnDecrypt.setAlpha(0.7f);
            btnEncrypt.setEnabled(false);
            btnEncrypt.setAlpha(0.7f);
        } else {
            btnDecrypt.setEnabled(true);
            btnDecrypt.setAlpha(1.0f);
            btnEncrypt.setEnabled(true);
            btnEncrypt.setAlpha(1.0f);
        }
    }

    @OnTextChanged(value=R.id.et_encryptor_password, callback=OnTextChanged.Callback.TEXT_CHANGED)
    public void onPasswordTextChanged(CharSequence s) {
        if (s.toString().trim().length() == 0) {
            btnDecrypt.setEnabled(false);
            btnDecrypt.setAlpha(0.7f);
            btnEncrypt.setEnabled(false);
            btnEncrypt.setAlpha(0.7f);
        } else if (etEnterText.getText().length() != 0){
            btnDecrypt.setEnabled(true);
            btnDecrypt.setAlpha(1.0f);
            btnEncrypt.setEnabled(true);
            btnEncrypt.setAlpha(1.0f);
        }
    }
}
