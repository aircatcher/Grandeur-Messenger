package xyz.grand.grandeur;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.PopupWindow;

/**
 * Created by Ferick Andrew on May 09, 2017.
 */

public class FriendDetailsPopUp extends Activity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_details_popup);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.friend_details_popup, null, false),100,100, true);
        pw.showAtLocation(this.findViewById(R.id.fragment_friends), Gravity.CENTER, 0, 0);
    }
}
