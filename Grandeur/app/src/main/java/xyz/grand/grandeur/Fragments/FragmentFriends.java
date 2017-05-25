package xyz.grand.grandeur.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.FragmentViews.AddFriendActivity;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.MainActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.adapter.UsersChatAdapter;
import xyz.grand.grandeur.model.User;
import xyz.grand.grandeur.settings.ProfileSettingsActivity;
import xyz.grand.grandeur.settings.SettingsActivity;

import static xyz.grand.grandeur.LoginActivity.mProgressDialog;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentFriends extends Fragment implements DialogFragmentProfileDetails.DialogFragmentProfileDetailsListener
{
    private static String TAG =  MainActivity.class.getSimpleName();

    protected ProgressBar mProgressBarForUsers;
    protected RecyclerView mUsersRecyclerView;

    private String mCurrentUserUid;
    private List<String>  mUsersKeyList;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUserRefDatabase;
    private ChildEventListener mChildEventListener;
    private UsersChatAdapter mUsersChatAdapter;

    // Firebase Disk Persistence (Maintain state when offline)
    private static FirebaseDatabase firebaseDatabase;
    public static FirebaseDatabase getDatabase()
    {
        if (firebaseDatabase == null)
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        View fragView = inflater.inflate(R.layout.fragment_friends, container, false);
        mProgressBarForUsers = (ProgressBar) fragView.findViewById(R.id.progress_bar_users);
        mUsersRecyclerView = (RecyclerView) fragView.findViewById(R.id.recycler_view_users);

        // Set Auth Instance
        mAuth = FirebaseAuth.getInstance();

        // Set Users Database
        mUserRefDatabase = FirebaseDatabase.getInstance().getReference().child("friendList");

        // Enable sync, keeping the app stay fresh when network is found
        mUserRefDatabase.keepSynced(true);

        // Set RecyclerView Adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mUsersRecyclerView.getContext(), linearLayoutManager.getOrientation());

        mUsersChatAdapter = new UsersChatAdapter(getActivity(), new ArrayList<User>());
        mUsersRecyclerView.setLayoutManager(linearLayoutManager);
        mUsersRecyclerView.setHasFixedSize(true);
        mUsersRecyclerView.addItemDecoration(dividerItemDecoration);
        mUsersRecyclerView.setAdapter(mUsersChatAdapter);

        // Set Users Key Lists
        mUsersKeyList = new ArrayList<>();

        // Set Auth Listener
         mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                hideProgressDialog();
                hideProgressBarForUsers();
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    setUserData(user);
                    queryAllUsers();
                }
                else
                {
                    // User is signed out
                    goToLogin();
                }
            }
        };

        if(isNetworkAvailable())
        {
            AdView mAdView = (AdView) fragView.findViewById(R.id.adView_friendsTab);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.bringToFront();
            mAdView.loadAd(adRequest);
        }

        return fragView;
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setUserData(FirebaseUser user) {
        mCurrentUserUid = user.getUid();
    }

    private void queryAllUsers()
    {
        mChildEventListener = getChildEventListener();
        mUserRefDatabase.limitToFirst(50).addChildEventListener(mChildEventListener);
    }

    private void goToLogin()
    {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        getActivity().finish();
        getActivity().startActivity(intent);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showProgressDialog();
        showProgressBarForUsers();
        mAuth.addAuthStateListener(mAuthListener);
        Toast.makeText(getActivity(), "Welcome back, " + mAuth, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        clearCurrentUsers();
        if (mChildEventListener != null) mUserRefDatabase.removeEventListener(mChildEventListener);
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }

    private void clearCurrentUsers()
    {
        mUsersChatAdapter.clear();
        mUsersKeyList.clear();
    }

    private void logout()
    {
        showProgressBarForUsers();
        setUserOffline();
        mAuth.signOut();
    }

    private void setUserOffline()
    {
        if(mAuth.getCurrentUser() != null )
        {
            Toast.makeText(getActivity(), R.string.signed_out, Toast.LENGTH_LONG).show();
            String userId = mAuth.getCurrentUser().getUid();
            mUserRefDatabase.child(userId).child("connection").setValue(UsersChatAdapter.OFFLINE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_add_friend)
        {
            Intent addNewFriend = new Intent(getActivity(), AddFriendActivity.class);
            getActivity().startActivity(addNewFriend);
            return true;
        }
        if(item.getItemId() == R.id.action_profile_settings)
        {
            Intent profileSettings = new Intent(getActivity(), ProfileSettingsActivity.class);
            getActivity().startActivity(profileSettings);
            return true;
        }
        if(item.getItemId() == R.id.action_about)
        {
            Intent intentAbout = new Intent(getActivity(), AboutActivity.class);
            getActivity().startActivity(intentAbout);
            return true;
        }
        if(item.getItemId() == R.id.action_settings)
        {
            Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
            getActivity().startActivity(intentSettings);
            return true;
        }
        if(item.getItemId() == R.id.action_sign_out)
        {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProgressBarForUsers(){
        mProgressBarForUsers.setVisibility(View.VISIBLE);
    }

    private void hideProgressBarForUsers()
    {
        if(mProgressBarForUsers.getVisibility()==View.VISIBLE)
            mProgressBarForUsers.setVisibility(View.GONE);
    }

    private void showProgressDialog()
    {
        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getActivity().getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.hide();
    }

    public void showProfileDialog(View view)
    {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragmentProfileDetails profileDetails = new DialogFragmentProfileDetails();
        profileDetails.setCancelable(true);
        profileDetails.show(fragmentManager, "Input Dialog");
    }
    @Override
    public void onFinishInputDialog(String profileName, String profileStatus) {}

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