package github.bandrews568.justencryptit.ui.file;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.EncryptionFileResult;
import github.bandrews568.justencryptit.model.FileListItem;
import github.bandrews568.justencryptit.utils.UiUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EncryptedFilesFragment extends Fragment implements OnListItemClickListener, OnEncryptActionClickListener, PasswordDialog.PasswordDialogListener {

    @BindView(R.id.recycler_view_encrypted_files) RecyclerView recyclerView;
    @BindView(R.id.view_empty_files_encrypted_files) LinearLayout linearLayoutEmptyFiles;

    private Unbinder unbinder;
    private FileViewModel viewModel;
    private List<FileListItem> files = new ArrayList<>();
    private EncryptedFilesRecyclerViewAdapter encryptedFilesRecyclerViewAdapter;
    private FileListItem fileListItem;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encrypted_files_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        encryptedFilesRecyclerViewAdapter = new EncryptedFilesRecyclerViewAdapter(getContext(), files);
        encryptedFilesRecyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(encryptedFilesRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(FileViewModel.class);
        viewModel.getEncryptedFilesLiveData().observe(this, this::handleEncryptedListResult);
        viewModel.getDecryptedLiveData().observe(this, this::handleDecryptionResult);
        viewModel.getCryptoProgressLiveData().observe(this, this::handleCryptoProgress);
        viewModel.populateFiles();
        files = viewModel.getEncryptedFilesList();
        encryptedFilesRecyclerViewAdapter.setValues(files);
        encryptedFilesRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

        toggleEmptyState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(FileListItem item) {
        // Show bottom sheet
        FileInfoBottomSheetFragment fileInfoBottomSheetFragment = FileInfoBottomSheetFragment.newInstance("encrypt");
        fileInfoBottomSheetFragment.setFileListItem(item);
        fileInfoBottomSheetFragment.setContext(getContext());
        fileInfoBottomSheetFragment.setListener(this);
        fileInfoBottomSheetFragment.show(requireFragmentManager(), null);
    }

    @Override
    public void onEncryptActionClick(FileListItem fileListItem) {
        this.fileListItem = fileListItem;

        // Show password dialog
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.setPasswordDialogListener(this);
        passwordDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onDialogSubmitClicked(String password) {
        if (fileListItem != null) {
            progressDialog = ProgressDialog.newInstance("Decrypt");
            progressDialog.show(getFragmentManager(), null);

            // Decrypt the fileListItem
            viewModel.decryptFile(password, fileListItem);
        }
    }

    @Override
    public void onDialogCancel() {
        fileListItem = null;
    }

    private void handleDecryptionResult(EncryptionFileResult result) {
        if (result.getError() != null) {
            UiUtils.errorDialog(getContext(), "Error decrypting file");
        } else {
            // Delete the fileListItem
            File file = new File(fileListItem.getLocation());
            if (!file.delete()) {
                System.out.println("Error deleting file");
            }
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        progressDialog = null;
    }

    private void handleEncryptedListResult(List<FileListItem> encryptedFileList) {
        files = encryptedFileList;
        encryptedFilesRecyclerViewAdapter.setValues(files);
        encryptedFilesRecyclerViewAdapter.notifyDataSetChanged();

        toggleEmptyState();
    }

    private void handleCryptoProgress(int progress) {
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    private void toggleEmptyState() {
        linearLayoutEmptyFiles.setVisibility(files.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }
}
