package github.bandrews568.justencryptit.ui.file;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.appbar.AppBarLayout;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import github.bandrews568.justencryptit.R;

public class FileFragment extends Fragment implements PasswordDialog.PasswordDialogListener {

    private static String TAG = FileFragment.class.getName();

    // Butterknife
    private Unbinder unbinder;

    private FileViewModel viewModel;

    private FilePickerDialog dialog;

    private String[] selectedFiles;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(FileViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
                    //Show dialog if the read permission has been granted.
                    dialog.show();
                }
            } else {
                //Permission has not been granted. Notify the user.
                Toast.makeText(getActivity(), "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
            }
        }
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

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT);
    }

    private void showPasswordDialog() {
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.setPasswordDialogListener(this);
        passwordDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onDialogSubmitClicked(String password) {
        // Encrypt the file with the password
        Log.d(TAG, "Password: " + password );
    }

    @Override
    public void onDialogCancel() {
        selectedFiles = null;
    }
}
