package xyz.grand.grandeur;

/**
 * Created by Ferick Andrew on Mar 22, 2017.
 */

public class FriendList {
    private String userAvatar;
    private String userName;
    private String userStatus;


    public FriendList(String userAvatar, String userName, String userStatus)
    {
        this.userAvatar = userAvatar;
        this.userName = userName;
        this.userStatus = userStatus;
    }

    public FriendList() {
    }

    public String getUserAvatar() { return userAvatar; }

    public String getUserName() {
        return userName;
    }

    public String getUserStatus() {
        return userStatus;
    }


    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}