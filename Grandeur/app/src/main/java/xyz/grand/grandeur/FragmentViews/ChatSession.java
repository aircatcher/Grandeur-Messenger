package xyz.grand.grandeur.FragmentViews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

import xyz.grand.grandeur.R;

public class ChatSession extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

//    private void displayChatMessage()
//    {
//        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_chat_item, FirebaseDatabase.getInstance().getReference())
//        {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//
//                //Get references to the views of list_chat_item.xml
//                TextView messageText = (TextView) v.findViewById(R.id.message_text);
//                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
//                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
//
//                messageText.setText(model.getMessageText());
//                messageUser.setText(model.getMessageUser());
//                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
//            }
//        };
//        historyListView.setAdapter(adapter);
//    }
}
