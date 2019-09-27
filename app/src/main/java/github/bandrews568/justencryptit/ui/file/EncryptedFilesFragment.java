package github.bandrews568.justencryptit.ui.file;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EncryptedFilesFragment extends Fragment implements OnListItemClickListener, OnEncryptActionClickListener {

    @BindView(R.id.recycler_view_encrypted_files) RecyclerView recyclerView;
    @BindView(R.id.view_empty_files_encrypted_files) LinearLayout linearLayoutEmptyFiles;

    private Unbinder unbinder;
    private FileViewModel viewModel;
    private List<FileListItem> files = new ArrayList<>();
    private EncryptedFilesRecyclerViewAdapter encryptedFilesRecyclerViewAdapter;

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
        // Show password dialog and decrypt the file
    }

    private void handleEncryptedListResult(List<FileListItem> encryptedFileList) {
        files = encryptedFileList;
        encryptedFilesRecyclerViewAdapter.setValues(files);
        encryptedFilesRecyclerViewAdapter.notifyDataSetChanged();

        toggleEmptyState();
    }

    private void toggleEmptyState() {
        linearLayoutEmptyFiles.setVisibility(files.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }
}
