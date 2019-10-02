package github.bandrews568.justencryptit.ui.file;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;

public class ProgressDialog extends DialogFragment {

    @BindView(R.id.tv_dialog_progress_message) TextView tvMessage;
    @BindView(R.id.progress_bar_dialog_progress) ProgressBar progressBar;
    @BindView(R.id.tv_dialog_progresstv_dialog_progress) TextView tvProgress;

    private Unbinder unbinder;
    private int progress;

    public static ProgressDialog newInstance(String type) {
        ProgressDialog progressDialog = new ProgressDialog();

        Bundle args = new Bundle();
        args.putString("type", type);

        progressDialog.setArguments(args);

        return progressDialog;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress, container);
        unbinder = ButterKnife.bind(this, view);
        setCancelable(false);

        if (getArguments() != null) {
            String type = getArguments().getString("type");

            if (type != null) {
                tvMessage.setText(type.equals("encrypt") ? "Encrypting file ..." : "Decrypting file ...");
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    public void setProgress(int progress) {
        this.progress = progress;

        tvProgress.setText(progress + "%");
        progressBar.setProgress(progress);
    }
}
