package github.bandrews568.justencryptit.ui.file;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;

public class PasswordDialog extends DialogFragment {

    public interface PasswordDialogListener {
        void onDialogSubmitClicked(String password);
        void onDialogCancel();
    }

    @BindView(R.id.til_dialog_password_confirm) TextInputLayout tilPasswordConfirm;
    @BindView(R.id.til_dialog_password_password) TextInputLayout tilPassword;

    private PasswordDialogListener passwordDialogListener;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_password, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(lp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        passwordDialogListener = null;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        if (passwordDialogListener != null) {
            passwordDialogListener.onDialogCancel();
        }
    }

    @OnClick(R.id.btn_dialog_password_submit)
    public void submitClicked() {
        tilPassword.setError(null);
        tilPasswordConfirm.setError(null);

        String passwordText = tilPassword.getEditText().getText().toString();
        String confirmPasswordText = tilPasswordConfirm.getEditText().getText().toString();

        boolean validPassword = true;

        if (TextUtils.isEmpty(passwordText)) {
            tilPassword.setError("Required");
            validPassword = false;
        }

        if (TextUtils.isEmpty(confirmPasswordText)) {
            tilPasswordConfirm.setError("Required");
            validPassword = false;
        } else if (!passwordText.equals(confirmPasswordText)) {
            tilPasswordConfirm.setError("Passwords must match");
            validPassword = false;
        }

        if (validPassword) {
            if (passwordDialogListener != null) {
                String padPassword = String.format("%1$-" + 16 + "s", passwordText).replace(' ', '0');
                passwordDialogListener.onDialogSubmitClicked(padPassword);
                dismiss();
            }
        }
    }

    @OnClick(R.id.btn_dialog_password_cancel)
    public void cancelClicked() {
        if (passwordDialogListener != null) {
            passwordDialogListener.onDialogCancel();
        }
        dismiss();
    }

    public void setPasswordDialogListener(PasswordDialogListener passwordDialogListener) {
        this.passwordDialogListener = passwordDialogListener;
    }
}
