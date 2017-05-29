package xyz.grand.grandeur.settings;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import xyz.grand.grandeur.BuildConfig;
import xyz.grand.grandeur.MainActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.model.ChatMessage;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch togglePinLock;
    public static Switch toggleDarkTheme;

    public static boolean useDarkTheme;
    public static String DARK_MODE;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    Intent dark_on;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        dark_on = new Intent(SettingsActivity.this, MainActivity.class);

        if(useDarkTheme)
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            setTheme(R.style.AppTheme_Dark);
            useDarkTheme = true;
            dark_on.putExtra(DARK_MODE, "1");
        }
        if(!(useDarkTheme))
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            setTheme(R.style.AppTheme);
            useDarkTheme = false;
            dark_on.putExtra(DARK_MODE, "0");
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_settings);

        togglePinLock = (Switch) findViewById(R.id.toggle_pinLock);
        toggleDarkTheme = (Switch) findViewById(R.id.toggle_dark_theme);
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);

        setSupportActionBar(toolbar);

        if(useDarkTheme)
        {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24dp));
            toolbar.setTitleTextColor(Color.WHITE);
        }
        if(!(useDarkTheme))
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
                    if(appUsageAccessEnabled()) { startActivity(new Intent(SettingsActivity.this, RequestForAppUsageAccessActivity.class)); }
                    else startActivity(new Intent(SettingsActivity.this, RequestForAppUsageAccessActivity.class));
                    isChecked = true;
                }
                else
                { /* Disable Pin Lock */ }
            }
        });

        toggleDarkTheme.setChecked(useDarkTheme);
        toggleDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                themeToggle(isChecked);
                if(isChecked)
                {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    private void themeToggle(boolean darkTheme)
    {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private boolean appUsageAccessEnabled()
    {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
