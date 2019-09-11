package github.bandrews568.justencryptit.ui.file;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private Unbinder unbinder;
    private FileObserver fileObserver;
    private List<FileListItem> files = new ArrayList<>();
    private DecryptedFilesRecyclerViewAdapter decryptedFilesRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decrypted_files_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        decryptedFilesRecyclerViewAdapter = new DecryptedFilesRecyclerViewAdapter(requireContext(), files);
        decryptedFilesRecyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(decryptedFilesRecyclerViewAdapter);
        return inflater.inflate(R.layout.fragment_decrypted_files, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        populateFilesList();

        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        if (directory.exists()) {
            if (fileObserver == null) {
                fileObserver = new FileObserver(directory.getPath(), FileObserver.ALL_EVENTS) {
                    @Override
                    public void onEvent(int event, @Nullable String path) {
                        // Only refresh the file list after new, deleted and renamed file events
                        if (event == FileObserver.CLOSE_WRITE || event == FileObserver.DELETE || event == FileObserver.MOVED_TO) {
                            getActivity().runOnUiThread(() -> {
                                if (recyclerView.getAdapter() != null) {
                                    populateFilesList();
                                }
                            });
                        }
                    }
                };
            }

            fileObserver.startWatching();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
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
        fileInfoBottomSheetFragment.show(requireFragmentManager(), null);
    }

    private void populateFilesList() {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        files.clear();

        if (!directory.exists() || directory.listFiles() == null) return;

        for (File file : directory.listFiles()) {
            // Need to check the file extensions to include any files that don't end in .jei
            if (!file.getName().endsWith(".jei")) {
                FileListItem fileListItem = new FileListItem();
                fileListItem.setLocation(file.getPath());
                fileListItem.setTime(file.lastModified());
                fileListItem.setFilename(file.getName());
                fileListItem.setSize(file.length());
                files.add(fileListItem);
            }
        }

        decryptedFilesRecyclerViewAdapter.notifyDataSetChanged();
    }
}
