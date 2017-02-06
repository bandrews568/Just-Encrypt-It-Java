package us.brandonandrews.justencryptit;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zoom_text, container);
    }

    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView userText = (TextView) view.findViewById(R.id.tvZoomText);
        Button btnClose = (Button) view.findViewById(R.id.btnClose);
        String text = getArguments().getString("text", "");
        userText.setText(text);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}
