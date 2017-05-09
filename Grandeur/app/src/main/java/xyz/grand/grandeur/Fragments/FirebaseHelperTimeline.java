package xyz.grand.grandeur.Fragments;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import xyz.grand.grandeur.FragmentViews.FriendList;
import xyz.grand.grandeur.FragmentViews.TimelineList;

/**
 * Created by Ferick Andrew on May 06, 2017.
 *
 * 1.SAVE DATA TO FIREBASE
 * 2. RETRIEVE
 * 3.RETURN AN ARRAYLIST
 */
public class FirebaseHelperTimeline
{
    DatabaseReference db;
    Boolean saved;
    ArrayList<TimelineList> timelineLists = new ArrayList<>();
    /*
 PASS DATABASE REFRENCE
  */
    public FirebaseHelperTimeline(DatabaseReference db) {
        this.db = db;
    }
    //WRITE IF NOT NULL
    public Boolean save(TimelineList timelineList)
    {
        if(timelineList == null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child("timelineList").push().setValue(timelineList);
                saved = true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
    }
    //IMPLEMENT FETCH DATA AND FILL ARRAYLIST
    private void fetchData(DataSnapshot dataSnapshot)
    {
        timelineLists.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            TimelineList timelineList = ds.getValue(TimelineList.class);
            timelineLists.add(timelineList);
        }
    }
    //RETRIEVE
    public ArrayList<TimelineList> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return timelineLists;
    }
}