package xyz.grand.grandeur;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import xyz.grand.grandeur.Fragments.FragmentChat;
import xyz.grand.grandeur.Fragments.FragmentFriends;

import static xyz.grand.grandeur.SettingsActivity.theme;

public class MainActivity extends AppCompatActivity
{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    TabLayout tabLayout;
    Toolbar toolbar;

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private String THEME_MODE;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Intent receiverIntent = getIntent();
        receiverIntent.getIntExtra("theme", theme);

        if(theme == 0)
        {
            setTheme(R.style.AppTheme);
            toolbar.setTitleTextColor(0xFFFFFF);
            theme++;
        }
        else
        {
            setTheme(R.style.AppTheme_Dark);
            toolbar.setTitleTextColor(0x000000);
            theme--;
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Tabs icons, texts, and colors
        tabLayout.getTabAt(0).setText("FRIENDS");
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_supervisor_account_black_24dp);
        tabLayout.getTabAt(1).setText("CHAT");
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_question_answer_black_24dp);
        tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#FF5252"));

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.bringToFront();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int tabPosition = tabLayout.getSelectedTabPosition();

        if(tabPosition == 0) getMenuInflater().inflate(R.menu.menu_friend, menu);
        else if(tabPosition == 1) getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() { return 2; } // Show 3 total pages.

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    FragmentFriends tab1 = new FragmentFriends();
                    return tab1;
                case 1:
                    FragmentChat tab2 = new FragmentChat();
                    return tab2;
                default:
                    return null;
            }
        }
    }
}
