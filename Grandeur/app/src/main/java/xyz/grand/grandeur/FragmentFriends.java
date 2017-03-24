package xyz.grand.grandeur;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentFriends extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        return rootView;
    }
}