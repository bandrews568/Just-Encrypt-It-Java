package github.bandrews568.justencryptit.ui.file;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.model.EncryptionFileResult;
import github.bandrews568.justencryptit.utils.UiUtils;

public class FileFragment extends Fragment implements PasswordDialog.PasswordDialogListener {

    @BindView(R.id.view_pager_file_fragment) ViewPager viewPager;
    @BindView(R.id.tab_layout_file_fragment) TabLayout tabLayout;
    @BindView(R.id.adView) AdView adView;

    // Butterknife
    private Unbinder unbinder;
    private FileViewModel viewModel;
    private FilePickerDialog dialog;
    private String[] selectedFiles;
    private ProgressDialog progressDialog;
    private FileViewModel.AsyncFileEncryption asyncFileEncryption;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PagerAdapter pagerAdapter = new FileTabsPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        MobileAds.initialize(requireContext(), initializationStatus -> {
            // TODO
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(FileViewModel.class);
        viewModel.getEncryptionLiveData().observe(this, this::handleEncryptionFileResult);
        viewModel.getCryptoProgressLiveData().observe(this, this::handleCryptoProgress);
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel.getFileObserver().startWatching();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        viewModel.populateFiles();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (asyncFileEncryption != null) {
            asyncFileEncryption.cancel(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onStop() {
        super.onStop();

        viewModel.getFileObserver().stopWatching();
    }

    @OnClick(R.id.fab_file_choose)
    public void fabChooseFileClicked() {
        showFilePickerDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (dialog != null) {
                    dialog.show();
                }
            } else {
                UiUtils.errorToast(getContext(), "Permission is required for getting list of files");
            }
        }
    }

    @Override
    public void onDialogSubmitClicked(String password) {
        // Encrypt the file with the password
        // Save all files to /JustEncryptIt
        // Create the directory if it doesn't exist
        // Save the encrypted file in the /JustEncryptIt directory with the extension .jei

        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "JustEncryptIt");

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                UiUtils.errorToast(getContext(), "Error making /JustEncryptIt directory");
                return;
            }
        }

        long freeDiskSpace = new File(Environment.getExternalStorageDirectory().getPath()).getFreeSpace();

        File inputFile = new File(selectedFiles[0]);

        if (inputFile.exists() && !inputFile.isDirectory()) {
            if (inputFile.length() > freeDiskSpace) {
                UiUtils.errorDialog(requireContext(), "Not enough free disk space to encrypt file");
                return;
            }
        }

        File outputFile = new File(directory, inputFile.getName() + ".jei");

        progressDialog = ProgressDialog.newInstance("encrypt");
        progressDialog.show(getFragmentManager(), null);

        asyncFileEncryption = viewModel.encryptFile(password, inputFile, outputFile);
    }

    @Override
    public void onDialogCancel() {
        selectedFiles = null;
    }

    private void showFilePickerDialog() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        dialog = new FilePickerDialog(getContext(), properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(files -> {
            selectedFiles = files;
            showPasswordDialog();
        });

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT);
    }

    private void showPasswordDialog() {
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.setPasswordDialogListener(this);
        passwordDialog.show(getFragmentManager(), null);
    }

    private void handleEncryptionFileResult(EncryptionFileResult encryptionFileResult) {
        if (encryptionFileResult.getError() != null) {
            UiUtils.errorDialog(getContext(), "Error encrypting file");
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        progressDialog = null;

        asyncFileEncryption = null;
    }

    private void handleCryptoProgress(int progress) {
        if (progressDialog != null && progress != -1 && asyncFileEncryption != null) {
            progressDialog.setProgress(progress);
        }
    }

    private class FileTabsPagerAdapter extends FragmentPagerAdapter {

        public FileTabsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new EncryptedFilesFragment();
            } else {
                return new DecryptedFilesFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Encrypted";
            } else {
                return "Decrypted";
            }
        }
    }
}
