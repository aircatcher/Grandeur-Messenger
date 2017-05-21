package xyz.grand.grandeur.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.OnClick;
import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.FireChatHelper.ExtraIntent;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.settings.SettingsActivity;
import xyz.grand.grandeur.adapter.MessageChatAdapter;
import xyz.grand.grandeur.adapter.UsersChatAdapter;
import xyz.grand.grandeur.model.ChatMessage;

/**
 * Created by Ferick Andrew on May 19, 2017.
 */

public class ChatActivity extends AppCompatActivity
{
    private static final String TAG = ChatActivity.class.getSimpleName();

    protected ProgressBar mProgressBarForChat;
    RecyclerView mChatRecyclerView;
    EditText mUserMessageChatText;
    Toolbar toolbar;

    private FirebaseAuth mAuth;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        // Set Auth Instance
        mAuth = FirebaseAuth.getInstance();

        // Set Database Instance
        String chatRef = this.getIntent().getStringExtra(ExtraIntent.EXTRA_CHAT_REF);
        messageChatDatabase = FirebaseDatabase.getInstance().getReference().child("1"); // chatRef grabs null, temporarily using "1" instead

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
                    if(newMessage.getSender().equals(mCurrentUserId))
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.SENDER);
                    else
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.RECIPIENT);
                    messageChatAdapter.refillAdapter(newMessage);
                    mChatRecyclerView.scrollToPosition(messageChatAdapter.getItemCount()-1);
                }
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
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if(messageChatListener != null)
            messageChatDatabase.removeEventListener(messageChatListener);
        messageChatAdapter.cleanUp();

    }

    @OnClick(R.id.btn_send_message)
    public void btnSendMsgListener(View sendButton)
    {
        String senderMessage = mUserMessageChatText.getText().toString().trim();

        if(!senderMessage.isEmpty())
        {
            ChatMessage newMessage = new ChatMessage(senderMessage,mCurrentUserId,mRecipientId);
            messageChatDatabase.push().setValue(newMessage);
            mUserMessageChatText.setText("");
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_about)
        {
            Intent intentAbout = new Intent(ChatActivity.this, AboutActivity.class);
            startActivity(intentAbout);
            return true;
        }
        if(item.getItemId() == R.id.action_settings)
        {
            Intent intentSettings = new Intent(ChatActivity.this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }
        if(item.getItemId() == R.id.action_sign_out)
        {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
