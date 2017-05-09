package xyz.grand.grandeur.Fragments;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;

import xyz.grand.grandeur.FragmentViews.FriendList;
import xyz.grand.grandeur.FragmentViews.TimelineList;
import xyz.grand.grandeur.R;

/**
 * Created by Ferick Andrew on May 06, 2017.
 *  1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */

public class CustomAdapterTimeline extends BaseAdapter
{
    Context c;
    ArrayList<TimelineList> timelineLists;

    public CustomAdapterTimeline(Context c, ArrayList<TimelineList> timelineLists) {
        this.c = c;
        this.timelineLists = timelineLists;
    }

    @Override
    public int getCount() {
        return timelineLists.size();
    }

    @Override
    public Object getItem(int position) {
        return timelineLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(c).inflate(R.layout.list_timeline_item,parent,false);
        }
        TextView tlUsername  = (TextView)  convertView.findViewById(R.id.timeline_username);
        TextView tlContent   = (TextView)  convertView.findViewById(R.id.timeline_content);
        TextView tlTime      = (TextView)  convertView.findViewById(R.id.timeline_time);
        ImageView tlImage    = (ImageView) convertView.findViewById(R.id.timeline_image);

        final TimelineList tl = (TimelineList) this.getItem(position);
//        userAvatar.setImageURI(s.getName());
        tlUsername.setText(tl.getUserName());
        tlContent.setText(tl.getTimelineContent());
        tlTime.setText(tl.getTimelineTime());
//        Uri imageUri = Uri.parse(###);
//        tlImage.setImageURI(tl.getTimelineImage());

        // On Item Click
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, tl.getUserName(),Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}