package us.brandonandrews.justencryptit.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import us.brandonandrews.justencryptit.R;

public class ZoomDialog extends DialogFragment {

    public ZoomDialog() {
        // Empty on purpose
    }

    public static ZoomDialog newInstance(String text) {
        ZoomDialog frag = new ZoomDialog();
        Bundle args = new Bundle();
        args.putString("text", text);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zoom_text, container);
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView userText = view.findViewById(R.id.tvZoomText);
        Button btnClose = view.findViewById(R.id.btnClose);

        if (getArguments() != null) {
            String text = getArguments().getString("text", "");
            userText.setText(text);
        }

        btnClose.setOnClickListener(v -> getDialog().dismiss());
    }
}
