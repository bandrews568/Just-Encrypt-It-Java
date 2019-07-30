package github.bandrews568.justencryptit.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import github.bandrews568.justencryptit.ui.about.AboutActivity;
import github.bandrews568.justencryptit.ui.encryptor.EncryptorFragment;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.ui.file.EncryptedFilesFragment;
import github.bandrews568.justencryptit.ui.file.FileFragment;
import github.bandrews568.justencryptit.ui.file.dummy.DummyContent;
import github.bandrews568.justencryptit.ui.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, new FileFragment())
                .commit();

        ((BottomNavigationView) findViewById(R.id.main_bottom_navigation)).setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment newFragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_file:
                newFragment = new FileFragment();
                ViewCompat.setElevation(findViewById(R.id.main_app_bar_layout), 0);
                break;
            case R.id.nav_text:
                newFragment = new EncryptorFragment();
                ViewCompat.setElevation(findViewById(R.id.main_app_bar_layout), 10.5f);
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, newFragment)
                .commit();

        return true;
    }
}