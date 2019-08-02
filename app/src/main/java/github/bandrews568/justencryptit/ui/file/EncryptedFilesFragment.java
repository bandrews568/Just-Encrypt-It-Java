package github.bandrews568.justencryptit.ui.file;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EncryptedFilesFragment extends Fragment {

    private static String TAG = FileFragment.class.getName();

    private List<FileListItem> files = new ArrayList<>();
    private OnEncryptedFilesFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        populateFilesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encrypted_files_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_encrypted_files);
        recyclerView.setAdapter(new EncryptedFilesRecyclerViewAdapter(getContext(), files, listener));
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void populateFilesList() {
        File[] internalFiles = getContext().getFilesDir().listFiles();

        for (File file : internalFiles) {
            FileListItem fileListItem = new FileListItem();
            fileListItem.setLocation(file.getPath());
            fileListItem.setTime(file.lastModified());
            fileListItem.setFilename(file.getName());
            fileListItem.setSize(file.length());
            files.add(fileListItem);
        }
    }

    public interface OnEncryptedFilesFragmentListener {
        void onListItemClick(FileListItem item);
    }

    public void setFiles(List<FileListItem> files) {
        this.files = files;
    }
}
