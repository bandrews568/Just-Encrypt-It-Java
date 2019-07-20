package github.bandrews568.justencryptit.ui.encryptor;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;

public class ResultDialog extends DialogFragment {

    @BindView(R.id.tvResultText) TextView tvResultText;

    private Unbinder unbinder;

    private ClipboardManager clipboard;

    public ResultDialog() {
        // Empty on purpose
    }

    public static ResultDialog newInstance(String text) {
        ResultDialog frag = new ResultDialog();
        Bundle args = new Bundle();
        args.putString("text", text);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_result, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (getArguments() != null) {
            String text = getArguments().getString("text", "");
            tvResultText.setText(text);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_copy)
    public void copyClicked() {
        if (clipboard != null) {
            ClipData text = ClipData.newPlainText("text", tvResultText.getText());
            clipboard.setPrimaryClip(text);
        }
    }

    @OnClick(R.id.btn_share)
    public void shareClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, tvResultText.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @OnClick(R.id.btn_close)
    public void closeClicked() {
        getDialog().dismiss();
    }
}
