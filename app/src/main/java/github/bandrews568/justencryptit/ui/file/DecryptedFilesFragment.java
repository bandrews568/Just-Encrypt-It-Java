package github.bandrews568.justencryptit.ui.file;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;


public class DecryptedFilesFragment extends Fragment implements OnListItemClickListener {

    @BindView(R.id.recycler_view_decrypted_files) RecyclerView recyclerView;
    @BindView(R.id.view_empty_files_decrypted_files) LinearLayout linearLayoutEmptyFiles;

    private Unbinder unbinder;
    private FileViewModel viewModel;
    private List<FileListItem> files = new ArrayList<>();
    private DecryptedFilesRecyclerViewAdapter decryptedFilesRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decrypted_files_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        decryptedFilesRecyclerViewAdapter = new DecryptedFilesRecyclerViewAdapter(requireContext(), files);
        decryptedFilesRecyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(decryptedFilesRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(FileViewModel.class);
        viewModel.getDecryptedFilesLiveData().observe(this, this::handleDecryptedListResult);
        viewModel.populateFiles();
        files = viewModel.getDecryptedFilesList();
        decryptedFilesRecyclerViewAdapter.setValues(files);
        decryptedFilesRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

        toggleEmptyState();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(FileListItem item) {
        // Show file sheet bottom dialog with the decrypted file options
        FileInfoBottomSheetFragment fileInfoBottomSheetFragment = FileInfoBottomSheetFragment.newInstance("decrypt");
        fileInfoBottomSheetFragment.setFileListItem(item);
        fileInfoBottomSheetFragment.setContext(getContext());
        fileInfoBottomSheetFragment.show(requireFragmentManager(), null);
    }

    private void toggleEmptyState() {
        linearLayoutEmptyFiles.setVisibility(files.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    private void handleDecryptedListResult(List<FileListItem> decryptedFileList) {
        files =  decryptedFileList;
        decryptedFilesRecyclerViewAdapter.setValues(files);
        decryptedFilesRecyclerViewAdapter.notifyDataSetChanged();

        toggleEmptyState();
    }
}
