package xyz.grand.grandeur.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ferick Andrew on 16/5/2017.
 */

@IgnoreExtraProperties
public class User {

    private String displayName, userStatus, email, password, connection;
    private int avatarId;
    private long createdAt;

    private String mRecipientId;

    public User() {
    }

    public User(String displayName, String userStatus, String email, String password, String connection, int avatarId, long createdAt) {
        this.displayName = displayName;
        this.userStatus = userStatus;
        this.email = email;
        this.password = password;
        this.connection = connection;
        this.avatarId = avatarId;
        this.createdAt = createdAt;
    }

    public String createUniqueChatRef(long createdAtCurrentUser, String currentUserEmail){
        String uniqueChatRef="";
        if(createdAtCurrentUser > getCreatedAt()){
            uniqueChatRef = cleanEmailAddress(currentUserEmail)+"-"+cleanEmailAddress(getUserEmail());
        }else {
            uniqueChatRef=cleanEmailAddress(getUserEmail())+"-"+cleanEmailAddress(currentUserEmail);
        }
        return uniqueChatRef;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    private String cleanEmailAddress(String email){
        //replace dot with comma since firebase does not allow dot
        return email.replace(".","-");
    }

    private String getUserEmail() {
        //Log.e("user email  ", userEmail);
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserStatus() {return userStatus;}

    public String getEmail() {
        return email;
    }

    public String getConnection() {
        return connection;
    }

    public int getAvatarId() {
        return avatarId;
    }

    @Exclude
    public String getRecipientId() {
        return mRecipientId;
    }

    public void setRecipientId(String recipientId) {
        this.mRecipientId = recipientId;
    }
}
