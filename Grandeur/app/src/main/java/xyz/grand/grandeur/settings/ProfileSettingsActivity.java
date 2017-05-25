package xyz.grand.grandeur.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import xyz.grand.grandeur.R;
import xyz.grand.grandeur.model.User;

import static xyz.grand.grandeur.LoginActivity.loginEmail;
import static xyz.grand.grandeur.LoginActivity.loginPwd;

public class ProfileSettingsActivity extends AppCompatActivity
{
    private static FirebaseDatabase firebaseDatabase;
    DatabaseReference mUserRefDatabase;

    private EditText changeDisplayName;
    private Button buttonSaveProfile;

    Editable changeName;
    String userId, displayName, userStatus, email, password, connection;
    int avatarId;
    long createdAt;

    public static FirebaseDatabase getDatabase()
    {
        if (firebaseDatabase == null)
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        changeDisplayName = (EditText) findViewById(R.id.edit_text_change_name);
        buttonSaveProfile = (Button) findViewById(R.id.button_profile_save);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_settings);
        setSupportActionBar(toolbar);

//        if(FirebaseDatabase.getInstance().getReference().child("friendList") == null)
//        {
//            loginEmail = null;
//            loginPwd = null;
//        }

        mUserRefDatabase = FirebaseDatabase.getInstance().getReference().child("friendList").child(userId).getRef();
        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName = changeDisplayName.getText();
                updateUser(displayName, userStatus, email, password, connection, avatarId, createdAt);
            }
        });
    }

    private void updateUser(String displayName, String userStatus, String email, String password, String connection, int avatarId, long createdAt)
    {
        User user = new User(displayName, userStatus, email, password, connection, avatarId, createdAt);
        mUserRefDatabase.child("displayName").setValue(changeName);
    }
}
