package xyz.grand.grandeur.Fragments;

/**
 * Created by Ferick Andrew on May 21, 2017.
 */

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import xyz.grand.grandeur.R;

public class DialogFragmentProfileDetails extends DialogFragment
{
    public static TextView profileName, profileStatus;
    static String DialogboxTitle;

    public interface DialogFragmentProfileDetailsListener
    {
        void onFinishInputDialog(String profileName, String profileStatus);
    }

    //---empty constructor required
    public DialogFragmentProfileDetails() {}

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState)
    {
        View view = inflater.inflate(R.layout.dialogfragment_profile_details, container);

        //---get the EditText and Button views
        profileName = (TextView) view.findViewById(R.id.tv_profile_name);
        profileStatus = (TextView) view.findViewById(R.id.tv_profile_status);

        //---show the keyboard automatically
        profileName.requestFocus();
        getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //---set the title for the dialog
        getDialog().setTitle(DialogboxTitle);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}