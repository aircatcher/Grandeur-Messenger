package xyz.grand.grandeur.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;

import xyz.grand.grandeur.R;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch togglePinLock;
    public static Switch toggleDarkTheme;

    public static boolean useDarkTheme;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if(useDarkTheme)
        {
            setTheme(R.style.AppTheme_Dark);
            useDarkTheme = true;
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_settings);

        togglePinLock = (Switch) findViewById(R.id.toggle_pinLock);
        toggleDarkTheme = (Switch) findViewById(R.id.toggle_dark_theme);
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        // Sorry for the redundant if-else for theme switch here
        // useDarkTheme can't be called after requestWindowFeature call

        if(useDarkTheme)
        {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24dp));
            toolbar.setTitleTextColor(Color.WHITE);
        }
        else
        {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp));
            toolbar.setTitleTextColor(Color.BLACK);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        togglePinLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                if(isChecked)
                {
                    // Enable & Setup PIN Lock
                    Intent enablePinLockIntent = new Intent(SettingsActivity.this, PinLockSetupActivity.class);
                    startActivity(enablePinLockIntent);
                }
                else
                {
                    // Disable PIN Lock
                }
            }
        });

        toggleDarkTheme.setChecked(useDarkTheme);
        toggleDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                themeToggle(isChecked);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void themeToggle(boolean darkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
