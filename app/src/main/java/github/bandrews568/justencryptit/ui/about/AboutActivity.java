package github.bandrews568.justencryptit.ui.about;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProviders;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import github.bandrews568.justencryptit.BuildConfig;
import github.bandrews568.justencryptit.R;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tv_about_version) TextView tvVersion;

    private AboutViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(AboutViewModel.class);
        tvVersion.setText(BuildConfig.VERSION_NAME);
    }

    @OnClick({R.id.llJustEncryptIt, R.id.llLicense, R.id.llAuthor, R.id.llSourceCode, R.id.llEmail})
    public void itemClicked(View v) {
        if (v.getId() == R.id.llLicense) {
            showLicenseDialog();
            return;
        }

        if (v.getId() == R.id.llEmail) {
            ShareCompat.IntentBuilder.from(this)
                    .setType("message/rfc822")
                    .addEmailTo("justencryptitapp@gmail.com")
                    .setChooserTitle("Send Email")
                    .startChooser();
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
