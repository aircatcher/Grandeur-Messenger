package xyz.grand.grandeur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class FragmentTimeline extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<TimelineList> adapter;
    RelativeLayout rl_timeline;
    ListView tmlnList;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sign_out)
        {
            AuthUI.getInstance().signOut(this.getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(rl_timeline, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
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
                Snackbar.make(rl_timeline, "Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                displayTimeline();
            }
            else{
                Snackbar.make(rl_timeline, "We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                getActivity().finish();
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

        tmlnList = (ListView) fragView.findViewById(R.id.timeline_list);
        rl_timeline = (RelativeLayout) fragView.findViewById(R.id.fragment_timeline);
        fab = (FloatingActionButton) fragView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EditText input_message = (EditText) v.findViewById(R.id.timeline_content);
//                FirebaseDatabase.getInstance().getReference().push().setValue(new TimelineList(input_message.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            }
        });

        return fragView;
    }

    private void displayTimeline()
    {
        // tmlnList = (ListView) getView().findViewById(R.id.timeline_list);  //This returns null reference, so put it on the onCreateView instead
        adapter = new FirebaseListAdapter<TimelineList>(this.getActivity(), TimelineList.class, R.layout.list_timeline_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, TimelineList model, int position)
            {
                //Get references to the views of list_item.xml
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