package github.bandrews568.justencryptit.ui.file;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;

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
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_delete)
    public void onDeleteClicked() {
        // Delete file
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_share)
    public void onShareClicked() {
        // Share file
    }

    @OnClick(R.id.ll_bottom_sheet_file_info_info)
    public void onInfoClicked() {
        // Show info dialog
    }

    public void setFileListItem(FileListItem fileListItem) {
        this.fileListItem = fileListItem;
    }
}
