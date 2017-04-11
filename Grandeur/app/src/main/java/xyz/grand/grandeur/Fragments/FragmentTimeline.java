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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;
import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.FragmentViews.PostTimeline;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.FragmentViews.TimelineList;
import xyz.grand.grandeur.SettingsActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentTimeline extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<TimelineList> adapter;

    ListView tmlnList;
    RelativeLayout rl_timeline;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_post_timeline)
        {
            Intent postTL = new Intent(getActivity(), PostTimeline.class);
            getActivity().startActivity(postTL);
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
            AuthUI.getInstance().signOut(this.getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(rl_timeline, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    Intent login = new Intent(getActivity(), LoginActivity.class);
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
                displayTimeline();
            }
            else
            {
                Intent login = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        ButterKnife.bind(getActivity());

        // Contents on Timeline Tab
        View fragView = inflater.inflate(R.layout.fragment_timeline, container, false);
        rl_timeline = (RelativeLayout) fragView.findViewById(R.id.fragment_timeline);
        tmlnList = (ListView) fragView.findViewById(R.id.timeline_list_view);
        fab = (FloatingActionButton) fragView.findViewById(R.id.fab_post_new_timeline);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTimeline = new Intent(getActivity(), PostTimeline.class);
                startActivity(newTimeline);
//              EditText input_message = (EditText) v.findViewById(R.id.timeline_content);
//              FirebaseDatabase.getInstance().getReference().push().setValue(new TimelineList(input_message.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            }
        });

        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Intent login = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
        }
        else
        {
            //Load content
            displayTimeline();
            //AlertDialog alertDialog = alertDialogBuilder.create();
            //alertDialog.show();
        }

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