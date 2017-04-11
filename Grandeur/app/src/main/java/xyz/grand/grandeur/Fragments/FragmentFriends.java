package xyz.grand.grandeur.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.FragmentViews.FriendList;
import xyz.grand.grandeur.FragmentViews.SearchFriendActivity;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.SettingsActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentFriends extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<FriendList> adapter;
    ListView frndList;
    RelativeLayout cl_login;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_friend)
        {
            Intent searchFriend = new Intent(getActivity(), SearchFriendActivity.class);
            getActivity().startActivity(searchFriend);
            return true;
        }
        else if(item.getItemId() == R.id.action_about)
        {
            Intent aboutPage = new Intent(getActivity(), AboutActivity.class);
            getActivity().startActivity(aboutPage);
            return true;
        }
        else if(item.getItemId() == R.id.action_settings)
        {
            Intent settings = new Intent(getActivity(), SettingsActivity.class);
            getActivity().startActivity(settings);
            return true;
        }
        else if(item.getItemId() == R.id.action_sign_out)
        {
            AuthUI.getInstance().signOut(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(cl_login, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().finish();
                    getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);

                }
            });
            return true;
        }
        else { return false; }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                displayFriendList();
            }
            else
            {
//              Toast.makeText(this.getActivity(), "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
//              getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
                Intent login = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
                Snackbar.make(cl_login, "We couldn't sign you in. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        View fragView = inflater.inflate(R.layout.fragment_friends, container, false);
        cl_login = (RelativeLayout) fragView.findViewById(R.id.fragment_friends);
        frndList = (ListView) fragView.findViewById(R.id.friend_list_view);



        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
//          getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
            Intent login = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
        }
        else
        {
            // Snackbar.make(rl_friends, "Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //Load content
            displayFriendList();
        }
        return fragView;
    }

    private void displayFriendList()
    {
        //frndList = (ListView) getView().findViewById(R.id.friend_list);   //This returns null reference, so put it on the onCreateView instead
        adapter = new FirebaseListAdapter<FriendList>(this.getActivity(), FriendList.class, R.layout.list_friend_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, FriendList model, int position)
            {
                //Get references to the views of list_friend_item.xml
                ImageView userAvatar = (ImageView) v.findViewById(R.id.friend_avatar);
                TextView userName = (TextView) v.findViewById(R.id.friend_username);
                TextView userStatus = (TextView) v.findViewById(R.id.friend_status);

                try {
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL("#AVATAR_URL_HERE").getContent());
                    userAvatar.setImageBitmap(bitmap);
                } catch (IOException e) { e.printStackTrace(); }
                userName.setText(model.getUserName());
                userStatus.setText(model.getUserStatus());
            }
        };
        frndList.setAdapter(adapter);
    }
}