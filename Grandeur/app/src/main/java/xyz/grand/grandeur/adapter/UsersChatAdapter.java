package xyz.grand.grandeur.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.grand.grandeur.FireChatHelper.ChatHelper;
import xyz.grand.grandeur.FireChatHelper.ExtraIntent;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.model.User;
import xyz.grand.grandeur.ui.ChatActivity;

import static xyz.grand.grandeur.FireChatHelper.ExtraIntent.EXTRA_CHAT_REF;

/**
 * Created by Ferick Andrew on 16/5/2017.
 */

public class UsersChatAdapter extends RecyclerView.Adapter<UsersChatAdapter.ViewHolderUsers>
{
    public static final String ONLINE = "ONLINE";
    public static final String OFFLINE = "OFFLINE";
    private List<User> mUsers;
    private Context mContext;
    private static String mCurrentUserEmail;
    private static Long mCurrentUserCreatedAt;
    private static String mCurrentUserId;

    public UsersChatAdapter(Context context, List<User> fireChatUsers)
    {
        mUsers = fireChatUsers;
        mContext = context;
    }

    @Override
    public ViewHolderUsers onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolderUsers(mContext,LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolderUsers holder, int position)
    {
        User fireChatUser = mUsers.get(position);

        // Set avatar
        int userAvatarId= ChatHelper.getDrawableAvatarId(fireChatUser.getAvatarId());
        Drawable avatarDrawable = ContextCompat.getDrawable(mContext, userAvatarId);
        holder.getUserAvatar().setImageDrawable(avatarDrawable);

        // Set display name & status
        holder.getUserDisplayName().setText(fireChatUser.getDisplayName());
        holder.getUserDisplayStatus().setText(fireChatUser.getUserStatus());

        // Set presence status
        holder.getStatusConnection().setText(fireChatUser.getConnection());

        // Set presence text color
        if(fireChatUser.getConnection().equals(ONLINE))
        {
            // Green color
            holder.getStatusConnection().setTextColor(Color.parseColor("#00FF00"));
        }
        else
        {
            // Red color
            holder.getStatusConnection().setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void refill(User users)
    {
        mUsers.add(users);
        notifyDataSetChanged();
    }

    public void changeUser(int index, User user)
    {
        mUsers.set(index,user);
        notifyDataSetChanged();
    }

    public void setCurrentUserInfo(String userUid, String email, long createdAt)
    {
        mCurrentUserId = userUid;
        mCurrentUserEmail = email;
        mCurrentUserCreatedAt = createdAt;
    }

    public void clear() {
        mUsers.clear();
    }

    /* ViewHolder for RecyclerView */
    public class ViewHolderUsers extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        int i = 1;
        private ImageView mUserAvatar;
        private TextView mUserDisplayName, mUserDisplayStatus;
        private TextView mStatusConnection;
        private Context mContextViewHolder;

        public ViewHolderUsers(Context context, View itemView)
        {
            super(itemView);
            mUserAvatar = (ImageView)itemView.findViewById(R.id.img_avatar);
            mUserDisplayName = (TextView)itemView.findViewById(R.id.text_view_display_name);
            mUserDisplayStatus = (TextView)itemView.findViewById(R.id.text_view_display_status);
            mStatusConnection = (TextView)itemView.findViewById(R.id.text_view_connection_status);
            mContextViewHolder = context;

            itemView.setOnClickListener(this);
        }

        public ImageView getUserAvatar() {
            return mUserAvatar;
        }

        public TextView getUserDisplayStatus() {
            return mUserDisplayStatus;
        }

        public TextView getUserDisplayName() {
            return mUserDisplayName;
        }
        public TextView getStatusConnection() {
            return mStatusConnection;
        }


        @Override
        public void onClick(View view)
        {
            User user = mUsers.get(getLayoutPosition());
            boolean chatView = true;

            // userCreatedAt; type long, returns null
//            String chatRef = user.createUniqueChatRef(mCurrentUserCreatedAt, mCurrentUserEmail);

            final Intent chatSenderIntent = new Intent(mContextViewHolder, ChatActivity.class);
            chatSenderIntent.putExtra(ExtraIntent.EXTRA_CURRENT_USER_ID, mCurrentUserId);
            chatSenderIntent.putExtra(ExtraIntent.EXTRA_RECIPIENT_ID, user.getRecipientId());
//            chatIntent.putExtra(ExtraIntent.EXTRA_CHAT_REF, chatRef);

            // Start new activity
            mContextViewHolder.startActivity(chatSenderIntent);
            chatSenderIntent.putExtra(EXTRA_CHAT_REF, i);
        }
    }
}
