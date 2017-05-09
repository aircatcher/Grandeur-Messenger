package xyz.grand.grandeur.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import xyz.grand.grandeur.AboutActivity;
import xyz.grand.grandeur.FragmentViews.FriendList;
import xyz.grand.grandeur.FragmentViews.SearchFriendActivity;
import xyz.grand.grandeur.FriendDetailsPopUp;
import xyz.grand.grandeur.LoginActivity;
import xyz.grand.grandeur.MainActivity;
import xyz.grand.grandeur.R;
import xyz.grand.grandeur.SettingsActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ferick Andrew on Mar 21, 2017.
 */

public class FragmentFriends extends Fragment
{
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<FriendList> adapter;
    ListView frndListView;
    Button btnPopupTester;
    RelativeLayout cl_login;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_friend)
        {
            Intent searchFriend = new Intent(getActivity(), SearchFriendActivity.class);
            getActivity().startActivity(searchFriend);
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
            AuthUI.getInstance().signOut(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    Snackbar.make(cl_login, "You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().finish();
                    getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);

                }
            });
            return true;
        }
        else { return false; }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                displayFriendList();
            }
            else
            {
//              Toast.makeText(this.getActivity(), "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
//              getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
                Intent login = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
                Snackbar.make(cl_login, "We couldn't sign you in. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        View fragView = inflater.inflate(R.layout.fragment_friends, container, false);
        cl_login = (RelativeLayout) fragView.findViewById(R.id.fragment_friends);
        frndListView = (ListView) fragView.findViewById(R.id.friend_list_view);
        btnPopupTester = (Button) fragView.findViewById(R.id.btn_test_popup);

        //frndListView.setAdapter(new ArrayAdapter< String > (this, R.layout.list_item, countries));
        btnPopupTester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FriendDetailsPopUp.class));
            }
        });
        frndListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView <? > arg0, View view, int position, long id) {
                startActivity(new Intent(getActivity(), FriendDetailsPopUp.class));
            }
        });

        // Check if not signed in
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
//          getActivity().startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
            Intent login = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(login, SIGN_IN_REQUEST_CODE);
            Toast.makeText(getActivity(), "You're not yet signed in, please sign in first", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Snackbar.make(rl_friends, "Welcome, " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //Load content
            displayFriendList();
        }
        return fragView;
    }

    private void displayFriendList()
    {
        //frndList = (ListView) getView().findViewById(R.id.friend_list);   //This returns null reference, so put it on the onCreateView instead
        adapter = new FirebaseListAdapter<FriendList>(this.getActivity(), FriendList.class, R.layout.list_friend_item, FirebaseDatabase.getInstance().getReference())
        {
            @Override
            protected void populateView(View v, FriendList model, int position)
            {
                //Get references to the views of list_friend_item.xml
                ImageView userAvatar = (ImageView) v.findViewById(R.id.friend_avatar);
                TextView userName    = (TextView)  v.findViewById(R.id.friend_username);
                TextView userStatus  = (TextView)  v.findViewById(R.id.friend_status);

//                Uri imageURI = Uri.parse(model.getUserAvatar());
//                userAvatar.setImageURI(imageURI);
                userName.setText(model.getUserName());
                userStatus.setText(model.getUserStatus());
            }
        };
        frndListView.setAdapter(adapter);
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            ImageView friendAvatar = (ImageView) view.findViewById(R.id.friend_avatar);
            TextView friendUsername = (TextView) view.findViewById(R.id.friend_username);
            TextView friendStatus   = (TextView) view.findViewById(R.id.friend_status);

            TextView frndUN = (TextView) view.findViewById(R.id.friend_detail_username);
            TextView frndST = (TextView) view.findViewById(R.id.friend_detail_status);

            String username = friendUsername.getText().toString();
            String status = friendStatus.getText().toString();
            Intent intent = new Intent(getActivity(), FriendDetailsPopUp.class);
            frndUN.setText(username);
            frndST.setText(status);
            startActivity(intent);

        }

    }
}

