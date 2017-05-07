package xyz.grand.grandeur.Fragments;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import xyz.grand.grandeur.FragmentViews.FriendList;
import java.util.ArrayList;

/**
 * Created by Ferick Andrew on May 06, 2017.
 *
 * 1.SAVE DATA TO FIREBASE
 * 2. RETRIEVE
 * 3.RETURN AN ARRAYLIST
 */
public class FirebaseHelperFriends
{
    DatabaseReference db;
    Boolean saved;
    ArrayList<FriendList> friendLists = new ArrayList<>();
    /*
 PASS DATABASE REFRENCE
  */
    public FirebaseHelperFriends(DatabaseReference db) {
        this.db = db;
    }
    //WRITE IF NOT NULL
    public Boolean save(FriendList friendList)
    {
        if(friendList == null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child("friendList").push().setValue(friendList);
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
        friendLists.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            FriendList friendList = ds.getValue(FriendList.class);
            friendLists.add(friendList);
        }
    }
    //RETRIEVE
    public ArrayList<FriendList> retrieve()
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
        return friendLists;
    }
}