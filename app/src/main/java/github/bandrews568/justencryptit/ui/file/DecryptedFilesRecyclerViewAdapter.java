package github.bandrews568.justencryptit.ui.file;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.FileListItem;

public class DecryptedFilesRecyclerViewAdapter extends RecyclerView.Adapter<DecryptedFilesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<FileListItem> values;
    private OnListItemClickListener listener;

    public DecryptedFilesRecyclerViewAdapter(Context context, List<FileListItem> items) {
        this.context = context;
        this.values = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_decrypted_files, parent, false);
        return new DecryptedFilesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileListItem fileListItem = values.get(position);

        holder.item = fileListItem;
        holder.tvName.setText(fileListItem.getFilename());
        holder.tvSize.setText(Formatter.formatFileSize(context, fileListItem.getSize()));
        holder.tvLastModified.setText("Last edited: " + DateFormat.format("MM/dd/yyyy hh:mm aa", fileListItem.getTime()));
        holder.linearLayout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.item);
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

        @BindView(R.id.tv_decrypted_files_name) TextView tvName;
        @BindView(R.id.tv_decrypted_files_size) TextView tvSize;
        @BindView(R.id.tv_decrypted_files_last_modified) TextView tvLastModified;
        @BindView(R.id.linear_layout_decrypted_files) LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setListener(OnListItemClickListener listener) {
        this.listener = listener;
    }
}
