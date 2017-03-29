package xyz.grand.grandeur;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentFriends extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<FriendList> adapter;
    RelativeLayout rl_friends;
    ListView frndList;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sign_out)
        {
            AuthUI.getInstance().signOut(this.getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(rl_friends, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    getActivity().finish();
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
                Snackbar.make(rl_friends, "Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).setDuration(5000).show();
                displayFriendList();
            }
            else{
                Snackbar.make(rl_friends, "We couldn't sign you in. Please try again later", Snackbar.LENGTH_LONG).setDuration(5000).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        View fragView = inflater.inflate(R.layout.fragment_friends, container, false);
        frndList = (ListView) fragView.findViewById(R.id.friend_list);
        rl_friends = (RelativeLayout) fragView.findViewById(R.id.fragment_friends);

        // Show Popup Alert
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
        final TextView popupText = new TextView(this.getActivity());
        popupText.setPadding(5, 5, 5, 5);
        String infoAlert = "Please sign out after this session before closing the app just for now.\nTemporary issue.";
        popupText.setTextSize(19);
        alertDialogBuilder.setView(popupText);
        popupText.setText(infoAlert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {}
        });

        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else
        {
            // Snackbar.make(rl_friends, "Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //Load content
            displayFriendList();
            //AlertDialog alertDialog = alertDialogBuilder.create();
            //alertDialog.show();
        }
        return fragView;
    }

    private void displayFriendList()
    {
        //frndList = (ListView) getView().findViewById(R.id.friend_list);   //This returns null reference, so put it on the onCreateView instead
        adapter = new FirebaseListAdapter<FriendList>(this.getActivity(), FriendList.class, R.layout.list_friend_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, FriendList model, int position) {

                //Get references to the views of list_item.xml
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