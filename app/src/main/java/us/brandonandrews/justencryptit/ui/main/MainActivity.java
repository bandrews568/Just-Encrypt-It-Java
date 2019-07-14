package us.brandonandrews.justencryptit.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import us.brandonandrews.justencryptit.R;
import us.brandonandrews.justencryptit.ui.about.AboutActivity;
import us.brandonandrews.justencryptit.ui.encryptor.EncryptorFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EncryptorFragment encryptorFragment = (EncryptorFragment) getSupportFragmentManager().findFragmentByTag("encryptor");

        if (encryptorFragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new EncryptorFragment(), "encryptor")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}