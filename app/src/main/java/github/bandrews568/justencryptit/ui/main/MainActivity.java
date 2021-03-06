package github.bandrews568.justencryptit.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import github.bandrews568.justencryptit.R;
import github.bandrews568.justencryptit.ui.about.AboutActivity;
import github.bandrews568.justencryptit.ui.encryptor.EncryptorFragment;
import github.bandrews568.justencryptit.ui.file.FileFragment;
import github.bandrews568.justencryptit.ui.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_bottom_navigation) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, new FileFragment())
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("selectedItem"));
        }
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
            case R.id.action_feedback:
                ShareCompat.IntentBuilder.from(this)
                    .setType("message/rfc822")
                    .addEmailTo("justencryptitapp@gmail.com")
                    .setSubject("App Feedback")
                    .setText("Feedback:")
                    .setChooserTitle("Send Feedback")
                    .startChooser();
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItem", bottomNavigationView.getSelectedItemId());
    }
}