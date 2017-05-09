package xyz.grand.grandeur;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
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
        LinearLayout friendPopupLayout = (LinearLayout) findViewById(R.id.friend_details_popup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

//        friendPopupLayout.getBackground().setAlpha(220);
        getWindow().setLayout((int) (width * .8), (int) (height * 0.6));
    }
}