package github.bandrews568.justencryptit.ui.file;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;
import github.bandrews568.justencryptit.ui.file.dummy.DummyContent;
import github.bandrews568.justencryptit.ui.file.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

public class EncryptedFilesFragment extends Fragment {

    private List<FileListItem> files;
    private OnEncryptedFilesFragmentListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EncryptedFilesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_encrypted_files_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setAdapter(new EncryptedFilesRecyclerViewAdapter(files, listener));
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnEncryptedFilesFragmentListener {
        void onListItemClick(FileListItem item);
    }

    public void setFiles(List<FileListItem> files) {
        this.files = files;
    }
}
