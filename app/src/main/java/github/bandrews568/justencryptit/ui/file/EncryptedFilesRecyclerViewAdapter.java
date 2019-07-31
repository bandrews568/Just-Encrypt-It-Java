package github.bandrews568.justencryptit.ui.file;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;

import java.util.List;


public class EncryptedFilesRecyclerViewAdapter extends RecyclerView.Adapter<EncryptedFilesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<FileListItem> values;
    private final EncryptedFilesFragment.OnEncryptedFilesFragmentListener listener;

    public EncryptedFilesRecyclerViewAdapter(Context context, List<FileListItem> items, EncryptedFilesFragment.OnEncryptedFilesFragmentListener listener) {
        this.context = context;
        this.values = items;
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
        FileListItem fileListItem = values.get(position);

        holder.item = fileListItem;
        holder.tvName.setText(fileListItem.getFilename());
        holder.tvSize.setText(Formatter.formatFileSize(context, fileListItem.getSize()));
        holder.tvLastModified.setText("Last edited: " + DateFormat.format("MM/dd/yyyy hh:mm aa", fileListItem.getTime()));
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
        public FileListItem item;
        public View view;

        @BindView(R.id.tv_encrypted_files_name) TextView tvName;
        @BindView(R.id.tv_encrypted_files_size) TextView tvSize;
        @BindView(R.id.tv_encrypted_files_last_modified) TextView tvLastModified;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
