package xyz.grand.grandeur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import xyz.grand.grandeur.adapter.UsersChatAdapter;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{
    public static ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    CoordinatorLayout cl_login;
    ProgressBar progressBar;
    AutoCompleteTextView inputEmail;
    EditText inputPassword;
    Button btnLogin, btnLoginGoogle, btnSignup, btnResetPassword;
    Toast toast;
    View toastView;

    public static String loginEmail, loginPwd;
    private static String loginPassword;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
//    private TextView mStatusTextView;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        cl_login = (CoordinatorLayout) findViewById(R.id.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLoginGoogle = (Button) findViewById(R.id.btn_login_with_google);
        btnSignup = (Button) findViewById(R.id.btn_sign_up);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        // [START customize_button]
        // Set the dimensions of the sign-in button.
//        btnLoginGoogle.setSize(SignInButton.SIZE_STANDARD);
        // [END customize_button]

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmail = inputEmail.getText().toString();
                loginPassword = inputPassword.getText().toString();

                if (TextUtils.isEmpty(loginEmail)) {
                    inputEmail.setError(getString(R.string.empty_email));
                    return;
                }

                if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()))
                {
                    inputEmail.setError("Incorrect email format");
                    return;
                }

                if (TextUtils.isEmpty(loginPassword)) {
                    inputPassword.setError(getString(R.string.empty_password));
                    return;
                }

                if (loginPassword.length() < 6) {
                    inputPassword.setError(getString(R.string.minimum_password));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (loginEmail.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        if(!isNetworkAvailable())
                                        {
                                            toast = Toast.makeText(LoginActivity.this, R.string.disconnected, Toast.LENGTH_LONG);
                                            toastView = toast.getView();
                                            toast.show();
                                        }
                                        else
                                        {
                                            toast = Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG);
                                            toastView = toast.getView();
                                            toast.show();
                                        }
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Not yet implemented, we're still fixing the issue", Toast.LENGTH_LONG).show();
                if(Auth.GoogleSignInApi == null) signIn();
                else
                {
                    Intent intentToMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentToMain);
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount googleAcct = result.getSignInAccount();
            assert googleAcct != null;
            String signInResult = getString(R.string.signed_in_fmt, googleAcct.getDisplayName());

            for(int hsir = 0; hsir < 1; hsir++)
            {
                Intent intentToMain =  new Intent(this, MainActivity.class);
                Toast.makeText(getApplicationContext(), signInResult, Toast.LENGTH_SHORT).show();
                startActivity(intentToMain);
            }
//            updateUI(true);
//        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });

        if(mAuth.getCurrentUser()!=null )
        {
            String userId = mAuth.getCurrentUser().getUid();
            userDatabase.child(userId).child("connection").setValue(UsersChatAdapter.OFFLINE);
        }
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
//        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

//    private void updateUI(boolean signedIn) {
//        if (signedIn) {
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.action_sign_out).setVisibility(View.VISIBLE);
//        } else {
//            Toast.makeText(getApplicationContext(), R.string.signed_out, Toast.LENGTH_SHORT).show();
////            Snackbar.make(cl_loginToMain, R.string.signed_out, Snackbar.LENGTH_SHORT);
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.action_sign_out).setVisibility(View.GONE);
//        }
//    }

    protected void setGooglePlusButtonText(SignInButton btnLoginProvider, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < btnLoginProvider.getChildCount(); i++) {
            View v = btnLoginProvider.getChildAt(i);

            buttonText = "SIGN IN WITH GOOGLE";
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    // Prevent going to MainActivity before login
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.action_sign_out:
                signOut();
                break;
//            case R.id.disconnect:
//                revokeAccess();
//                break;
        }
    }
}