package xyz.grand.grandeur.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.ButterKnife;
import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.FragmentViews.ChatMessage;
import xyz.grand.grandeur.FragmentViews.FriendToChat;
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
    private FirebaseListAdapter<ChatMessage> adapter;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> chat_list = new ArrayList<>();
    private DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference().getRoot();

    ListView historyListView;
    RelativeLayout rl_chat;
    EditText input_message;
    FloatingActionButton fab_send_message;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_new_chat)
        {
            Intent newChat = new Intent(getActivity(), FriendToChat.class);
            getActivity().startActivity(newChat);
            return true;
        }
        else if(item.getItemId() == R.id.action_about)
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
            AuthUI.getInstance().signOut(this.getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(rl_chat, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
                }
            });
            return true;
        }
        else { return false; }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//    {
//        inflater.inflate(R.menu.menu_mainr, menu);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                displayChatHistoryLV();
            }
            else
            {
                Intent login = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        // Message Contents on Chat Tab
        View fragView = inflater.inflate(R.layout.list_chat_history_item, container, false);
        rl_chat = (RelativeLayout) fragView.findViewById(R.id.fragment_chat);
        historyListView = (ListView) fragView.findViewById(R.id.chat_history_list_view);
        input_message = (EditText) fragView.findViewById(R.id.input_message);

        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Intent login = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
        }
        else
        {
            //Load content
            displayChatHistoryLV();
            //AlertDialog alertDialog = alertDialogBuilder.create();
            //alertDialog.show();
        }

        return fragView;
    }

//    private void displayChatHistoryLV()
//    {
//        /**
//         *  This is just a testing so I can get to know more to the ListView layout
//         **/
//        final String[] chatHistoryFriendName = {"friend1", "friend2", "friend3", "friend4", "friend5", "friend6", "friend7", "friend8", "friend9", "friend10", "friend11", "friend12", "friend13", "friend14", "friend15"};
//        final String[] chatHistoryMessages = {"Hello!", "How are you doing?", "Hi there", "What are the assignments?", "Sure I hope so", "Why not", "Yes I assume that you do that", "This is a message", "Can I call you now?", "Testing testing 123", "testtesttest", "setsetset set", "yes and no", "hahaha", "weeeeee"};
//
//        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.list_chat_history_item, FirebaseDatabase.getInstance().getReference())
//        {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//
//                //Get references to the views of list_chat_item.xml
//                TextView messageText = (TextView) v.findViewById(R.id.message_text);
//                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
//                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
//
//                int i = 0;
//                while(chatHistoryFriendName != null)
//                {
//                    messageUser.setText(chatHistoryFriendName[i]);
//                    messageText.setText(chatHistoryMessages[i]);
//                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
//                    i++;
//                }
//            }
//        };
//        historyListView.setAdapter(adapter);
//    }

    private void displayChatHistoryLV()
    {
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.list_chat_history_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                //Get references to the views of list_chat_item.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                if(messageText == null)
                {
                    messageText.setText("This lab is lonely\nTo start a chat, go to Friends tab, and select one of your friend.");
                }
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };
//        historyListView.setAdapter(adapter);
    }
}