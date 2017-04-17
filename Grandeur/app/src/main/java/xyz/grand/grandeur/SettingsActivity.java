package xyz.grand.grandeur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private ImageView btnBackToMenu;
    private Toolbar toolbar;
    private Switch toggleDarkTheme;

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private int theme = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            theme++;
            setTheme(R.style.AppTheme_Dark);
            int backButtonColor = ContextCompat.getColor(this, R.color.white);
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_settings);

        toggleDarkTheme = (Switch) findViewById(R.id.toggle_dark_theme);
        btnBackToMenu = (ImageView) findViewById(R.id.btn_settings_backToMenu);
        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        if(theme == 1)
        {
//            btnBackToMenu.getDrawable().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN );
            btnBackToMenu.setBackgroundColor(0x5d4037);
            btnBackToMenu.setImageResource(R.drawable.ic_arrow_back_24dp);
            toolbar.setTitleTextColor(Color.WHITE);
            theme--;
        }
        else
        {
            toolbar.setTitleTextColor(Color.BLACK);
            theme--;
        }

        final Intent senderIntent = new Intent(this, MainActivity.class);
        senderIntent.putExtra("theme", theme);

//        if(useDarkTheme)
//        {
//            setTheme(R.style.AppTheme_Dark);
//            int backButtonColor = ContextCompat.getColor(this, R.color.white);
//            btnBackToMenu.setColorFilter(backButtonColor, PorterDuff.Mode.SRC_IN);
//            toolbar.setTitleTextColor(Color.WHITE);
//        }

//        btnBackToMenu.getDrawable().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN );
        btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(senderIntent);
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
