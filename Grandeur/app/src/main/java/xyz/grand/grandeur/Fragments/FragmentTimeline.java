package xyz.grand.grandeur.Fragments;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import xyz.grand.grandeur.R;
import xyz.grand.grandeur.SettingsActivity;
import xyz.grand.grandeur.FragmentViews.TimelineList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentTimeline extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<TimelineList> adapter;
    RelativeLayout rl_timeline;
    ListView tmlnList;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings)
        {
            AuthUI.getInstance().signOut(this.getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Intent settings = new Intent(getActivity(), SettingsActivity.class);
                    getActivity().startActivity(settings);
                }
            });
            return true;
        }
        else if(item.getItemId() == R.id.action_sign_out)
        {
            AuthUI.getInstance().signOut(this.getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(rl_timeline, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
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
                Toast.makeText(this.getActivity(), "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                displayTimeline();
            }
            else
            {
                Toast.makeText(this.getActivity(), "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
                getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        // Contents on Timeline Tab
        View fragView = inflater.inflate(R.layout.fragment_timeline, container, false);

        tmlnList = (ListView) fragView.findViewById(R.id.timeline_list_view);
        rl_timeline = (RelativeLayout) fragView.findViewById(R.id.fragment_timeline);
        fab = (FloatingActionButton) fragView.findViewById(R.id.fab_post_new_timeline);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EditText input_message = (EditText) v.findViewById(R.id.timeline_content);
//                FirebaseDatabase.getInstance().getReference().push().setValue(new TimelineList(input_message.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            }
        });

        displayTimeline();
        return fragView;
    }

    private void displayTimeline()
    {
        // tmlnList = (ListView) getView().findViewById(R.id.timeline_list);  //This returns null reference, so put it on the onCreateView instead
        adapter = new FirebaseListAdapter<TimelineList>(this.getActivity(), TimelineList.class, R.layout.fragment_timeline, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, TimelineList model, int position)
            {
                //Get references to the views of list_timeline_item.xml
                TextView timelineUsername, timelineTime, timelineContent, timelineURL;
                timelineUsername = (TextView) v.findViewById(R.id.timeline_username);
                timelineTime = (TextView) v.findViewById(R.id.timeline_time);
                timelineContent = (TextView) v.findViewById(R.id.timeline_content);
                timelineURL = (TextView) v.findViewById(R.id.timeline_url);

                timelineUsername.setText(model.getUserName());
                timelineTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimelineTime()));
                timelineContent.setText(model.getTimelineContent());
                timelineURL.setText(model.getTimelineURL());
            }
        };
        tmlnList.setAdapter(adapter);
    }
}