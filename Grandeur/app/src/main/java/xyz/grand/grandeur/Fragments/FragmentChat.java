package xyz.grand.grandeur.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.SettingsActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentChat extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
//    private FirebaseListAdapter<ChatList> adapter;
    ListView chatListView;
    RelativeLayout cl_login;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_about)
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
            AuthUI.getInstance().signOut(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(cl_login, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().finish();
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
                displayChatList();
            }
            else
            {
//              Toast.makeText(this.getActivity(), "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
//              getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
                Intent login = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
                Snackbar.make(cl_login, "We couldn't sign you in. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        View fragView = inflater.inflate(R.layout.fragment_chat, container, false);
        cl_login = (RelativeLayout) fragView.findViewById(R.id.fragment_chat);
        chatListView = (ListView) fragView.findViewById(R.id.chat_history_list_view);

        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
//          getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
            Intent login = new Intent(getActivity(), LoginActivity.class);
            getActivity().finish();
            startActivityForResult(login, SIGN_IN_REQUEST_CODE);
            Toast.makeText(getActivity(), "You're not yet signed in, please sign in first", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Snackbar.make(rl_chat, "Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //Load content
            displayChatList();
        }
        return fragView;
    }

    private void displayChatList()
    {
//        adapter = new FirebaseListAdapter<ChatList>(this.getActivity(), ChatList.class, R.layout.list_chat_history_item, FirebaseDatabase.getInstance().getReference().child("chatMessage"))
//        {
//            @Override
//            protected void populateView(View v, ChatList model, int position)
//            {
//                //Get references to the views of list_chat_history_item.xml
//                TextView messageUser = (TextView) v.findViewById(R.id.ch_message_user);
//                TextView messageText = (TextView) v.findViewById(R.id.ch_message_text);
//                TextView messageTime = (TextView) v.findViewById(R.id.ch_message_time);
//
//                messageUser.setText(model.getMessageUser());
//                messageText.setText(model.getMessageText());
//                messageTime.setText(model.getMessageTime());
//            }
//        };
//        chatListView.setAdapter(adapter);
    }
}