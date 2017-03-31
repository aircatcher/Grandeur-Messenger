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

    ListView msgListView;
    RelativeLayout rl_chat;
    EditText input_message;
    FloatingActionButton fab_send_message;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_about)
        {
            Intent settings = new Intent(getActivity(), AboutActivity.class);
            getActivity().startActivity(settings);
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
//        inflater.inflate(R.menu.menu_main, menu);
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
                displayChatMessage();
            }
            else
            {
                Toast.makeText(this.getActivity(), "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
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
        ButterKnife.bind(getActivity());

        // Message Contents on Chat Tab
        View fragView = inflater.inflate(R.layout.fragment_chat, container, false);
        rl_chat = (RelativeLayout) fragView.findViewById(R.id.fragment_chat);
        msgListView = (ListView) fragView.findViewById(R.id.message_list_view);
        input_message = (EditText) fragView.findViewById(R.id.input_message);
        fab_send_message = (FloatingActionButton) fragView.findViewById(R.id.fab_send_message);

        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Intent login = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Toast.makeText(this.getActivity(), "Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
            //Load content
            displayChatMessage();
            //AlertDialog alertDialog = alertDialogBuilder.create();
            //alertDialog.show();
        }

        fab_send_message.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance().getReference().push()
                        .setValue(new ChatMessage(input_message.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        );

                // Clear the input
                input_message.setText("");
            }
        });

        return fragView;
    }

    private void displayChatMessage()
    {
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.list_chat_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                //Get references to the views of list_chat_item.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };
        msgListView.setAdapter(adapter);
    }
}