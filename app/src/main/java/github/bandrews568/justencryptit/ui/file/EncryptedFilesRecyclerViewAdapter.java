package github.bandrews568.justencryptit.ui.file;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;

import java.util.List;


public class EncryptedFilesRecyclerViewAdapter extends RecyclerView.Adapter<EncryptedFilesRecyclerViewAdapter.ViewHolder> {

    private final List<FileListItem> values;
    private final EncryptedFilesFragment.OnEncryptedFilesFragmentListener listener;

    public EncryptedFilesRecyclerViewAdapter(List<FileListItem> items, EncryptedFilesFragment.OnEncryptedFilesFragmentListener listener) {
        values = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_encrypted_files, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = values.get(position);
        holder.tvName.setText(values.get(position).getFilename());
        holder.tvSize.setText(String.valueOf(values.get(position).getSize()));

        holder.view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListItemClick(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public final TextView tvName;
        public final TextView tvSize;
        public final TextView tvLastModified;
        public FileListItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.tvName = view.findViewById(R.id.tv_encrypted_files_name);
            this.tvSize = view.findViewById(R.id.tv_encrypted_files_size);
            this.tvLastModified = view.findViewById(R.id.tv_encrypted_files_last_modified);
        }
    }
}
