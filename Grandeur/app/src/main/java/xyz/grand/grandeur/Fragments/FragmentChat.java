package xyz.grand.grandeur.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.OnClick;
import xyz.grand.grandeur.FireChatHelper.ExtraIntent;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.adapter.MessageChatAdapter;
import xyz.grand.grandeur.model.ChatMessage;

/**
 * Created by Ferick Andrew on May 19, 2017.
 */

public class FragmentChat extends Fragment
{
    private static final String TAG = FragmentChat.class.getSimpleName();

    RecyclerView mChatRecyclerView;
    EditText mUserMessageChatText;

    private String mRecipientId;
    private String mCurrentUserId;
    private MessageChatAdapter messageChatAdapter;
    private DatabaseReference messageChatDatabase;
    private ChildEventListener messageChatListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View fragView = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatRecyclerView = (RecyclerView) fragView.findViewById(R.id.recycler_view_chat);
        mUserMessageChatText = (EditText) fragView.findViewById(R.id.edit_text_message);

        // Set Database Instance
        String chatRef = getActivity().getIntent().getStringExtra(ExtraIntent.EXTRA_CHAT_REF);
        messageChatDatabase = FirebaseDatabase.getInstance().getReference().child("1"); // chatRef grabs null, temporarily using "1" instead

        // Set Users ID
        mRecipientId = getActivity().getIntent().getStringExtra(ExtraIntent.EXTRA_RECIPIENT_ID);
        mCurrentUserId = getActivity().getIntent().getStringExtra(ExtraIntent.EXTRA_CURRENT_USER_ID);

        // Set Chat's RecyclerView
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatRecyclerView.setHasFixedSize(true);
        messageChatAdapter = new MessageChatAdapter(new ArrayList<ChatMessage>());
        mChatRecyclerView.setAdapter(messageChatAdapter);

        return fragView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        messageChatListener = messageChatDatabase.limitToFirst(20).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                if(dataSnapshot.exists()){
                    ChatMessage newMessage = dataSnapshot.getValue(ChatMessage.class);
                    if(newMessage.getSender().equals(mCurrentUserId)){
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.SENDER);
                    }else{
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.RECIPIENT);
                    }
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
    public void onStop() {
        super.onStop();

        if(messageChatListener != null) {
            messageChatDatabase.removeEventListener(messageChatListener);
        }
        messageChatAdapter.cleanUp();

    }

    @OnClick(R.id.btn_send_message)
    public void btnSendMsgListener(View sendButton){

        String senderMessage = mUserMessageChatText.getText().toString().trim();

        if(!senderMessage.isEmpty()){

            ChatMessage newMessage = new ChatMessage(senderMessage,mCurrentUserId,mRecipientId);
            messageChatDatabase.push().setValue(newMessage);

            mUserMessageChatText.setText("");
        }
    }
}
