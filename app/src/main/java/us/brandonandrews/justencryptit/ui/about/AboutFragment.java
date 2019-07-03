package us.brandonandrews.justencryptit.ui.about;

import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import us.brandonandrews.justencryptit.R;

public class AboutFragment extends Fragment {

    // Butterknife
    private Unbinder unbinder;

    private AboutViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AboutViewModel.class);
    }

    @OnClick({R.id.llJustEncryptIt, R.id.llChangeLog, R.id.llLicense, R.id.llAuthor, R.id.llSourceCode})
    public void itemClicked(View v) {
        if (v.getId() == R.id.llLicense) {
            showLicenseDialog();
            return;
        }


        Uri uri = null;

        switch (v.getId()) {
            case R.id.llJustEncryptIt:
                uri = viewModel.justEncryptItUri();
                break;
            case R.id.llChangeLog:
                uri = viewModel.changeLogUri();
                break;
            case R.id.llAuthor:
                uri = viewModel.authorUri();
                break;
            case R.id.llSourceCode:
                uri = viewModel.sourceCodeUri();
                break;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showLicenseDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_license);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ((TextView) dialog.findViewById(R.id.tvLicense)).setText(getString(R.string.mit_license, currentYear));

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
