package github.bandrews568.justencryptit.ui.file;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;
import github.bandrews568.justencryptit.utils.UiUtils;

public class FileInfoBottomSheetFragment extends BottomSheetDialogFragment {

    private Unbinder unbinder;

    private FileListItem fileListItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_file_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_decrypt)
    public void onDecryptClicked() {
        // Show password dialog
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_edit)
    public void onEditClicked() {
        // Show edit name dialog
        dismiss();
        FragmentActivity activity = requireActivity();
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
                UiUtils.errorToast(activity, "Error renaming file");
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
                showErrorToast("Error deleting file");
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_share)
    public void onShareClicked() {
        // Share file
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_info)
    public void onInfoClicked() {
        dismiss();
        FileDetailsDialog fileDetailsDialog = new FileDetailsDialog();
        fileDetailsDialog.setFileListItem(fileListItem);
        fileDetailsDialog.show(getFragmentManager(), null);
    }

    private void showErrorToast(String message) {
        Toast toast = new Toast(getActivity());

        View custom_view = getLayoutInflater().inflate(R.layout.toast_error, null);
        ((TextView) custom_view.findViewById(R.id.tv_toast_message)).setText(message);

        toast.setView(custom_view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public void setFileListItem(FileListItem fileListItem) {
        this.fileListItem = fileListItem;
    }
}
