package github.bandrews568.justencryptit.ui.file;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import github.bandrews568.justencryptit.model.FileListItem;

public class FileDetailsDialog extends DialogFragment {

    @BindView(R.id.tv_dialog_file_details_name) TextView tvName;
    @BindView(R.id.tv_dialog_file_details_size) TextView tvSize;
    @BindView(R.id.tv_dialog_file_details_last_modified) TextView tvLastModified;
    @BindView(R.id.tv_dialog_file_details_path) TextView tvPath;

    private Unbinder unbinder;

    private FileListItem fileListItem;

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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_file_details, container);
        unbinder = ButterKnife.bind(this, view);
        setupUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ib_dialog_file_details_close, R.id.btn_dialog_file_details_ok})
    public void btnClicked() {
        dismiss();
    }

    private void setupUI() {
        if (fileListItem == null) return;

        tvName.setText(fileListItem.getFilename());
        tvSize.setText(Formatter.formatFileSize(getContext(), fileListItem.getSize()));
        tvLastModified.setText(DateFormat.format("MM/dd/yyyy hh:mm aa", fileListItem.getTime()));
        tvPath.setText(fileListItem.getLocation());
    }

    public void setFileListItem(FileListItem fileListItem) {
        this.fileListItem = fileListItem;
    }
}
