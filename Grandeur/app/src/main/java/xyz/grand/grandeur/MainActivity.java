package xyz.grand.grandeur;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import butterknife.BindView;
import xyz.grand.grandeur.Fragments.FragmentFriends;
import xyz.grand.grandeur.adapter.UsersChatAdapter;
import xyz.grand.grandeur.model.User;
import xyz.grand.grandeur.ui.PinLockActivity;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;
import static xyz.grand.grandeur.settings.PinLockSetupActivity.pinLockValue;
import static xyz.grand.grandeur.settings.SettingsActivity.DARK_MODE;
import static xyz.grand.grandeur.settings.SettingsActivity.useDarkTheme;

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
    private static String TAG =  MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private Toast toast;
    private Snackbar welcomeSnackbar;
    View toastView;
    @BindView(R.id.progress_bar_users) ProgressBar mProgressBarForUsers;
    @BindView(R.id.recycler_view_users) RecyclerView mUsersRecyclerView;

    private String mCurrentUserUid, currentUserName;
    private List<String> mUsersKeyList;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUserRefDatabase;
    private ChildEventListener mChildEventListener;
    private UsersChatAdapter mUsersChatAdapter;

    private static SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        String darkMode = getIntent().getStringExtra(DARK_MODE);
        if (darkMode == "1")
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            setTheme(R.style.AppTheme_Dark);
            useDarkTheme = true;
        }
        else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            setTheme(R.style.AppTheme);
            useDarkTheme = false;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    mUser = user;
                    Toast.makeText(MainActivity.this, "USER ID\n" + mUser.getUid(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        if(mAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else
        {
            mUser = mAuth.getCurrentUser();

            View mainContent = findViewById(R.id.main_content);
            welcomeSnackbar = Snackbar.make(mainContent, "Welcome, " + mUser.getEmail(), Snackbar.LENGTH_LONG);
            welcomeSnackbar.show();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        if (!isNetworkAvailable())
        {
            toast = Toast.makeText(MainActivity.this, R.string.disconnected, Toast.LENGTH_LONG);
            toastView = toast.getView();
            toast.show();
        }

        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras == null) pinLockValue = null;
            else pinLockValue = extras.getString("pinLockValue");
        }
        else pinLockValue = (String) savedInstanceState.getSerializable("pinLockValue");

        if(pinLockValue != null) startActivity(new Intent(MainActivity.this, PinLockActivity.class));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() { return 1; } // Show 3 total pages.

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    toolbar.setTitle(R.string.app_name);
                    welcomeSnackbar.show();
                    return new FragmentFriends();
                default:
                    return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
//        int tabPosition = tabLayout.getSelectedTabPosition();

//        if(tabPosition == 0)
        getMenuInflater().inflate(R.menu.menu_friend, menu);
//        else if(tabPosition == 1) getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    private void showProgressBarForUsers(){
        mProgressBarForUsers.setVisibility(View.VISIBLE);
    }

    private void hideProgressBarForUsers()
    {
        if(mProgressBarForUsers.getVisibility()==View.VISIBLE)
            mProgressBarForUsers.setVisibility(View.GONE);
    }

    private ChildEventListener getChildEventListener()
    {
        return new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if(dataSnapshot.exists())
                {
                    String userUid = dataSnapshot.getKey();

                    if(dataSnapshot.getKey().equals(mCurrentUserUid))
                    {
                        User currentUser = dataSnapshot.getValue(User.class);
                        mUsersChatAdapter.setCurrentUserInfo(userUid, currentUser.getEmail(), currentUser.getCreatedAt());
                    }
                    else
                    {
                        User recipient = dataSnapshot.getValue(User.class);
                        recipient.setRecipientId(userUid);
                        mUsersKeyList.add(userUid);
                        mUsersChatAdapter.refill(recipient);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                if(dataSnapshot.exists())
                {
                    String userUid = dataSnapshot.getKey();
                    if(!userUid.equals(mCurrentUserUid))
                    {
                        User user = dataSnapshot.getValue(User.class);
                        int index = mUsersKeyList.indexOf(userUid);
                        if(index > -1) mUsersChatAdapter.changeUser(index, user);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
    }
}
