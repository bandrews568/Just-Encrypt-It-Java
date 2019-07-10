package us.brandonandrews.justencryptit.ui.about;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import us.brandonandrews.justencryptit.R;

public class AboutActivity extends AppCompatActivity {

    private AboutViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(AboutViewModel.class);
    }

    @OnClick({R.id.llJustEncryptIt, R.id.llLicense, R.id.llAuthor, R.id.llSourceCode})
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_license);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ((TextView) dialog.findViewById(R.id.tvLicense)).setText(getString(R.string.mit_license, currentYear));

        dialog.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
