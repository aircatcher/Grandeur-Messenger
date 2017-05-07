package xyz.grand.grandeur.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.FragmentViews.FriendList;
import java.util.ArrayList;

/**
 * Created by Ferick Andrew on May 06, 2017.
 *  1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */

public class CustomAdapterFriends extends BaseAdapter
{
    Context c;
    ArrayList<FriendList> friendLists;
    public CustomAdapterFriends(Context c, ArrayList<FriendList> spacecrafts) {
        this.c = c;
        this.friendLists = friendLists;
    }
    @Override
    public int getCount() {
        return friendLists.size();
    }
    @Override
    public Object getItem(int position) {
        return friendLists.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(c).inflate(R.layout.list_friend_item,parent,false);
        }
        ImageView userAvatar = (ImageView) convertView.findViewById(R.id.friend_avatar);
        TextView userName    = (TextView)  convertView.findViewById(R.id.friend_username);
        TextView userStatus  = (TextView)  convertView.findViewById(R.id.friend_status);
        final FriendList s = (FriendList) this.getItem(position);
//        userAvatar.setImageURI(s.getName());
        userName.setText(s.getUserName());
        userStatus.setText(s.getUserStatus());

        // On Item Click
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, s.getUserName(),Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}