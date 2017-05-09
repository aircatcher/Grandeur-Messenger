package xyz.grand.grandeur.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.FragmentViews.FriendList;
import xyz.grand.grandeur.FragmentViews.PostTimeline;
import xyz.grand.grandeur.FragmentViews.SearchFriendActivity;
import xyz.grand.grandeur.FragmentViews.TimelineList;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.SettingsActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentTimeline extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<TimelineList> adapter;
    FirebaseHelperTimeline firebaseHelper;

    ListView timelineListView;
    RelativeLayout rl_timeline;
    FloatingActionButton fab_post_new_timeline;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_post_timeline)
        {
            Intent postNewTimeline = new Intent(getActivity(), PostTimeline.class);
            getActivity().startActivity(postNewTimeline);
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
                    Snackbar.make(rl_timeline, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
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
                displayTimelineList();
            }
            else
            {
//              Toast.makeText(this.getActivity(), "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
//              getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
                Intent login = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
                Snackbar.make(rl_timeline, "We couldn't sign you in. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        View fragView = inflater.inflate(R.layout.fragment_timeline, container, false);
        rl_timeline = (RelativeLayout) fragView.findViewById(R.id.fragment_timeline);
        timelineListView = (ListView) fragView.findViewById(R.id.timeline_list_view);
        fab_post_new_timeline = (FloatingActionButton) fragView.findViewById(R.id.fab_post_new_timeline);

        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
//          getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
            Intent login = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
            Toast.makeText(getActivity(), "You're not yet signed in, please sign in first", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Snackbar.make(rl_friends, "Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //Load content
            displayTimelineList();
        }

        fab_post_new_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInputDialog();
            }
        });

        return fragView;
    }

    private void displayTimelineList()
    {
        //frndList = (ListView) getView().findViewById(R.id.friend_list);   //This returns null reference, so put it on the onCreateView instead
        adapter = new FirebaseListAdapter<TimelineList>(this.getActivity(), TimelineList.class, R.layout.list_timeline_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, TimelineList model, int position)
            {
                //Get references to the views of list_timeline_item.xml
                TextView tl_username = (TextView) v.findViewById(R.id.timeline_username);
                TextView tl_time = (TextView) v.findViewById(R.id.timeline_time);
                ImageView tl_image = (ImageView) v.findViewById(R.id.timeline_image);
                TextView tl_content = (TextView) v.findViewById(R.id.timeline_content);

//                Uri imageURI = Uri.parse(model.getTimelineImage());
                tl_username.setText(model.getUserName());
                tl_time.setText(model.getTimelineTime());
//                tl_image.setImageURI(imageURI);
                tl_content.setText(model.getTimelineContent());
            }
        };
        timelineListView.setAdapter(adapter);
    }

    private void displayInputDialog()
    {
        Dialog d = new Dialog(getActivity());
        d.setTitle("Save To Firebase");
        d.setContentView(R.layout.input_dialog_timeline);
        final EditText etNewContent = (EditText) d.findViewById(R.id.et_new_content);
        final EditText etNewImage   = (EditText) d.findViewById(R.id.et_new_image);
        final Button   btnPostTL    = (Button) d.findViewById(R.id.btn_post_this_timeline);

        btnPostTL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // GET DATA
                String tlContent = etNewContent.getText().toString();
                String tlImage = etNewImage.getText().toString();

                // SET DATA
                TimelineList tl = new TimelineList();
                tl.setTimelineContent(tlContent);
                tl.setTimelineImage(tlImage);

                // SIMPLE VALIDATION
                if (tlContent != null && tlContent.length() > 0) {
                    //THEN SAVE
                    if (firebaseHelper.save(tl)){
                        etNewContent.setText("");
                        etNewImage.setText("");
//                        adapter = new CustomAdapterTimeline(getActivity(), firebaseHelper.retrieve());
                        timelineListView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getActivity(), "Content cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        d.show();
    }
}