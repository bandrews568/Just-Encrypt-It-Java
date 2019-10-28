package github.bandrews568.justencryptit.ui.file;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.BuildConfig;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;
import github.bandrews568.justencryptit.utils.UiUtils;

public class FileInfoBottomSheetFragment extends BottomSheetDialogFragment {

    @BindView(R.id.tv_bottom_dialog_file_info_type) TextView tvType;
    @BindView(R.id.iv_bottom_sheet_file_info_lock) ImageView ivLock;

    private Unbinder unbinder;
    private Context context;
    private FileListItem fileListItem;
    private OnEncryptActionClickListener listener;

    public static FileInfoBottomSheetFragment newInstance(String type) {
        FileInfoBottomSheetFragment fileInfoBottomSheetFragment = new FileInfoBottomSheetFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        fileInfoBottomSheetFragment.setArguments(args);

        return fileInfoBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_file_info, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            String type = getArguments().getString("type");

            if (type != null) {
                tvType.setText(type.equals("encrypt") ? "Decrypt" : "Encrypt");

                Drawable lockDrawable = getResources()
                        .getDrawable(type.equals("encrypt") ?  R.drawable.ic_lock_open : R.drawable.ic_lock);
                ivLock.setImageDrawable(lockDrawable);
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_decrypt)
    public void onEncryptionActionClicked() {
        // Show password dialog
        if (listener != null) {
            listener.onEncryptActionClick(fileListItem);
            dismiss();
        }
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_edit)
    public void onEditClicked() {
        // Show edit name dialog
        dismiss();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_rename_file, null, false);

        EditText input = view.findViewById(R.id.et_rename_file);
        input.setText(fileListItem.getFilename());
        input.setFocusableInTouchMode(true);
        input.requestFocus();

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Rename", (dialog, which) -> {
            String newFileName = input.getText().toString();
            File file = new File(fileListItem.getLocation());
            File newFile = new File(file.getParent(), newFileName);

            if (!file.renameTo(newFile)) {
                if (context != null) {
                    UiUtils.errorDialog(context, "Error renaming file");
                }
            }

            dialog.dismiss();
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_delete)
    public void onDeleteClicked() {
        // Delete file
        dismiss();

        String message = String.format("Are you sure you want to delete \"<b>%s</b>\"?", fileListItem.getFilename());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setPositiveButton("Delete", (dialog, which) -> {
            File file = new File(fileListItem.getLocation());
            if (!file.delete()) {
                UiUtils.errorDialog(context, "Error deleting file");
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_share)
    public void onShareClicked() {
        File fileToShare = new File(fileListItem.getLocation());

        Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".files", fileToShare);

        ShareCompat.IntentBuilder.from((Activity) context)
                .setType("*/*")
                .setStream(contentUri)
                .startChooser();
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_info)
    public void onInfoClicked() {
        dismiss();
        FileDetailsDialog fileDetailsDialog = new FileDetailsDialog();
        fileDetailsDialog.setFileListItem(fileListItem);
        fileDetailsDialog.show(getFragmentManager(), null);
    }

    public void setFileListItem(FileListItem fileListItem) {
        this.fileListItem = fileListItem;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(OnEncryptActionClickListener listener) {
        this.listener = listener;
    }
}
