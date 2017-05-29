package xyz.grand.grandeur.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.OnClick;
import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.FireChatHelper.ExtraIntent;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.SignupActivity;
import xyz.grand.grandeur.settings.SettingsActivity;
import xyz.grand.grandeur.adapter.MessageChatAdapter;
import xyz.grand.grandeur.adapter.UsersChatAdapter;
import xyz.grand.grandeur.model.ChatMessage;

import static xyz.grand.grandeur.FireChatHelper.ExtraIntent.EXTRA_CHAT_REF;

/**
 * Created by Ferick Andrew on May 19, 2017.
 */

public class ChatActivity extends AppCompatActivity
{
    private static final String TAG = ChatActivity.class.getSimpleName();
    private int chatCount = 1;
    protected ProgressBar mProgressBarForChat;
    RecyclerView mChatRecyclerView;
    EditText mUserMessageChatText;
    Button btnSendMessage;
    Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mRecipientId;
    private String mCurrentUserId;
    private MessageChatAdapter messageChatAdapter;
    private DatabaseReference messageChatDatabase;
    private ChildEventListener messageChatListener;

    // Firebase Disk Persistence (Maintain state when offline)
    private static FirebaseDatabase firebaseDatabase;

    public static FirebaseDatabase getDatabase()
    {
        if (firebaseDatabase == null)
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mProgressBarForChat = (ProgressBar) findViewById(R.id.progress_for_chat);
        mChatRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mUserMessageChatText = (EditText) findViewById(R.id.edit_text_message);
        btnSendMessage = (Button) findViewById(R.id.btn_send_message);
        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        // Set Auth Instance
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else
        {
            mUser = mAuth.getCurrentUser();
        }

        // Set Database Instance
        if( FirebaseDatabase.getInstance().getReference().child("extraChatRef") == null )
        {
            messageChatDatabase = FirebaseDatabase.getInstance().getReference();
            messageChatDatabase.child("extraChatRef").push();
        }
        messageChatDatabase = FirebaseDatabase.getInstance().getReference().child("extraChatRef");
        messageChatDatabase.push();

        // Enable sync, keeping the app stay fresh when network is found
        messageChatDatabase.keepSynced(true);

        // Set Users ID
        mRecipientId = this.getIntent().getStringExtra(ExtraIntent.EXTRA_RECIPIENT_ID);
        mCurrentUserId = this.getIntent().getStringExtra(ExtraIntent.EXTRA_CURRENT_USER_ID);

        // Set Chat's RecyclerView
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setHasFixedSize(true);
        messageChatAdapter = new MessageChatAdapter(new ArrayList<ChatMessage>());
        mChatRecyclerView.setAdapter(messageChatAdapter);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senderMessage = mUserMessageChatText.getText().toString().trim();

                if(!senderMessage.isEmpty())
                {
                    ChatMessage newMessage = new ChatMessage(senderMessage,mCurrentUserId,mRecipientId);
                    messageChatDatabase.push().setValue(newMessage);
                    mUserMessageChatText.setText("");
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_delete_chat)
        {
            eraseChatFromDatabase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        messageChatListener = messageChatDatabase.limitToFirst(20).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey)
            {
                if(dataSnapshot.exists())
                {
                    ChatMessage newMessage = dataSnapshot.getValue(ChatMessage.class);

                    if( newMessage.getSender() == mUser.getUid() )
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.SENDER);
                    else
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.RECIPIENT);

                    messageChatAdapter.refillAdapter(newMessage);
                    mChatRecyclerView.scrollToPosition(messageChatAdapter.getItemCount()-1);
                }

                Log.e(dataSnapshot.getKey(),dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        messageChatDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    chatCount = Log.e(snap.getKey(),snap.getChildrenCount() + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if(messageChatListener != null)
            messageChatDatabase.removeEventListener(messageChatListener);
        messageChatAdapter.cleanUp();

    }

//    @OnClick(R.id.btn_send_message)
//    public void btnSendMsgListener(View sendButton)
//    {
//        String senderMessage = mUserMessageChatText.getText().toString().trim();
//
//        if(!senderMessage.isEmpty())
//        {
//            ChatMessage newMessage = new ChatMessage(senderMessage,mCurrentUserId,mRecipientId);
//            messageChatDatabase.push().setValue(newMessage);
//            mUserMessageChatText.setText("");
//        }
//    }

    private void eraseChatFromDatabase()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This chat will be erased completely").setTitle("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        messageChatDatabase.removeValue();
                        finish();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void goToLogin()
    {
        Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        finish();
        startActivity(intent);
    }

    private void logout()
    {
        showProgressBarForUsers();
        setUserOffline();
        mAuth.signOut();
    }

    private void setUserOffline()
    {
        if(mAuth.getCurrentUser()!=null )
        {
            String userId = mAuth.getCurrentUser().getUid();
            messageChatDatabase.child(userId).child("connection").setValue(UsersChatAdapter.OFFLINE);
        }
    }

    private void showProgressBarForUsers(){
        mProgressBarForChat.setVisibility(View.VISIBLE);
    }

    private void hideProgressBarForUsers()
    {
        if(mProgressBarForChat.getVisibility()==View.VISIBLE)
            mProgressBarForChat.setVisibility(View.GONE);
    }
}
