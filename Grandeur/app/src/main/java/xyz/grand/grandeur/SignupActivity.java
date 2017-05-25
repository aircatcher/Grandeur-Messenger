package xyz.grand.grandeur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import xyz.grand.grandeur.adapter.UsersChatAdapter;
import xyz.grand.grandeur.model.User;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final String TAG = "SignupActivity";
    private static int i = 1;
    Uri imageUri;
    ProgressDialog progDial;

    String userStatus, displayName, signUpEmail, signUpPassword, connection, uid;
    int avatarId;
    long createdAt;

    private EditText etDisplayName, inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnAddAvatar, btnResetPassword;
    public ImageView avatarPreview;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRefDatabase;

    // Firebase Disk Persistence (Maintain state when offline)
    private DatabaseReference mDatabaseRef;
    private static FirebaseDatabase firebaseDatabase;
    public FirebaseDatabase getDatabase()
    {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        if (firebaseDatabase == null)
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }

//    final Firebase ref = new Firebase("https://grandeur-fb0de.firebaseio.com") {
//    ref.authWithPassword("jenny@example.com", "correcthorsebatterystaple",
//            new Firebase.AuthResultHandler() {
//            @Override
//            public void onAuthenticated(AuthData authData)
//            {
//                // Authentication just completed successfully :)
//                Map<String, String> map = new HashMap<>();
//                map.put("provider", authData.getProvider());
//                if(authData.getProviderData().containsKey("displayName")) {
//                    map.put("displayName", authData.getProviderData().get("displayName").toString());
//                }
//                ref.child("users").child(authData.getUid()).setValue(map);
//            }
//
//            @Override
//            public void onAuthenticationError(FirebaseError error) {
//                // Something went wrong :(
//            }
//        });

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference avatarRef;

    StorageReference storageRef = storage.getReference();

    // Create a reference with an initial file path and name
    StorageReference pathReference = storageRef.child("avatar/avatar" + i + ".jpg");

    // Create a reference to a file from a Google Cloud Storage URI
    StorageReference gsReference = storage.getReferenceFromUrl("gs://grandeur-fb0de.appspot.com/avatar/avatar" + i + ".jpg");

    // Create a reference from an HTTPS URL
    // Note that in the URL, characters are URL escaped!
//    StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/avatar%20avatar" + i + ".jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // Set Users Database
        mUserRefDatabase = FirebaseDatabase.getInstance().getReference().child("friendList");

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        etDisplayName = (EditText) findViewById(R.id.edit_text_register_display_name);
        inputEmail = (EditText) findViewById(R.id.edit_text_register_email);
        inputPassword = (EditText) findViewById(R.id.edit_text_register_password);

        btnAddAvatar = (Button) findViewById(R.id.add_your_avatar);
        avatarPreview = (ImageView) findViewById(R.id.avatar_preview);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayName = etDisplayName.getText().toString().trim();
                signUpEmail = inputEmail.getText().toString().trim();
                signUpPassword = inputPassword.getText().toString().trim();

                if (avatarPreview.getDrawable().getConstantState() == ContextCompat.getDrawable(SignupActivity.this, R.drawable.ic_person_black_24dp).getConstantState()) {
                    Toast.makeText(getApplicationContext(), "Your avatar cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(displayName)) {
                    etDisplayName.setError("Display name cannot be empty");
                    return;
                }

                if (TextUtils.isEmpty(signUpEmail)) {
                    inputEmail.setError(getString(R.string.empty_email));
                    return;
                }

                if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(signUpEmail).matches()))
                {
                    inputEmail.setError("Incorrect email format");
                    return;
                }

                if (TextUtils.isEmpty(signUpPassword)) {
                    inputPassword.setError(getString(R.string.empty_password));
                    return;
                }

                if (signUpPassword.length() < 6) {
                    inputPassword.setError(getString(R.string.minimum_password));
                    return;
                }

                if (imageUri != null) uploadAvatar();

                progressBar.setVisibility(View.VISIBLE);
                //create user
                mAuth.createUserWithEmailAndPassword(signUpEmail, signUpPassword)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mUserRefDatabase.setValue(i);
                                do
                                {
                                    uid = String.valueOf(i);
                                    i++;
                                }
                                while(mUserRefDatabase != null);

                                mUserRefDatabase.setValue(i);
                                mUserRefDatabase.child(uid).child("connection").setValue(UsersChatAdapter.ONLINE);
                                writeNewUser(uid, displayName, userStatus, signUpEmail, signUpPassword, connection, avatarId, createdAt);

                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    private void writeNewUser(String userId, String displayName, String userStatus, String email, String password, String connection, int avatarId, long createdAt)
    {
        userId = String.valueOf(i);
        displayName = etDisplayName.toString();
        email = inputEmail.toString();
        password = inputPassword.toString();
        connection = UsersChatAdapter.ONLINE;
        avatarId = avatarPreview.getId();

        User user = new User(displayName, userStatus, email, password, connection, avatarId, createdAt);
        mDatabaseRef.child("friendList").setValue(i);
        mDatabaseRef.child("friendList").child(userId).setValue(user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) signInAnonymously();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/jpg");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void uploadAvatar()
    {
        progDial = new ProgressDialog(SignupActivity.this);
        progDial.setMessage("Uploading your avatar ...");
        progDial.show();

        avatarRef = storageRef.child("avatar/avatar" + i + ".jpg");
        File localFile = null;
        try {
            localFile = File.createTempFile("avatar" + i, "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        avatarRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                progDial.dismiss();
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progDial.dismiss();
                // Handle any errors
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            avatarPreview.setImageURI(imageUri);
            btnAddAvatar.setText("Replace Image");

            if(!isNetworkAvailable())
            {
                Toast toast = Toast.makeText(this, "Network not found, your avatar won't show properly next time\nTry to reselect the image again", Toast.LENGTH_LONG);
                View toastView = toast.getView();
                toastView.setBackgroundColor(0x850707);
                toastView.setBackgroundResource(R.drawable.toast_drawable_error);
                toast.show();
            }
//            else
//            {
//                if(imageUri != null)
//                {
////                    // This function is continuously looping since the httpsReference is impossible for now.
//////                    while(!(imageUri == null)) {
//////                        imageUri = Uri.parse(storageRef.child("avatar" + i + ".jpg").toString());
//////                        i++;
//////                    }
////                    i = 5;
////
//                    avatarRef = storageRef.child("avatar/avatar" + i + ".jpg");
//                    File localFile = null;
//                    try {
//                        localFile = File.createTempFile("avatar" + i, "jpg");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    avatarRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            // Local temp file has been created
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle any errors
//                        }
//                    });
//                }
//                else {
//                    Toast.makeText(SignupActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
//                }
//            }
        }
    }
}