package com.example.arturarzumanyan.taskmanager.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.auth.AccessTokenAsyncTask;
import com.example.arturarzumanyan.taskmanager.auth.TokenAsyncTaskEvents;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity implements TokenAsyncTaskEvents {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mApiClient;
    private AccessTokenAsyncTask mAccessTokenAsyncTask;
    private String mAccessToken;
    private String mRefreshToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar.events"),
                               new Scope("https://www.googleapis.com/auth/tasks"))
                .build();

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,  new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }

                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            updateUI(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl().toString());

        }
    }

    private void updateUI(String userName, String userEmail, String userPhotoUrl) {
        Toast.makeText(getApplicationContext(), "User logged in", Toast.LENGTH_LONG).show();

        Intent accountIntent = new Intent(SignInActivity.this, IntentionActivity.class);
        accountIntent.putExtra("userName", userName);
        accountIntent.putExtra("userEmail", userEmail);
        accountIntent.putExtra("userPhotoUrl", userPhotoUrl);
        startActivity(accountIntent);
        finish();
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);
                //String authCode = acct.getServerAuthCode();
                //String userName = acct.getDisplayName();
                //String userPhotoUrl = acct.getPhotoUrl().toString();
                //mAccessTokenAsyncTask = new AccessTokenAsyncTask(this);
                //mAccessTokenAsyncTask.execute(authCode);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        String authCode = acct.getServerAuthCode();
        mAccessTokenAsyncTask = new AccessTokenAsyncTask(this);
        mAccessTokenAsyncTask.execute(authCode);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                        } else {

                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mAccessTokenAsyncTask != null) {
            mAccessTokenAsyncTask.cancel(false);
            mAccessTokenAsyncTask = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPostExecute(String buffer) throws JSONException {
        String accessToken = getAccessTokenFromBuffer(buffer);
        String refreshToken = getRefreshTokenFromBuffer(buffer);

        SharedPreferences prefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accessToken", accessToken);
        editor.putString("refreshToken", refreshToken);
        editor.apply();
        Toast.makeText(SignInActivity.this, accessToken, Toast.LENGTH_SHORT).show();
    }

    private String getAccessTokenFromBuffer(String buffer) throws JSONException {
        JSONObject object = new JSONObject(buffer);
        mAccessToken = object.getString("access_token");
        return mAccessToken;
    }

    private String getRefreshTokenFromBuffer(String buffer) throws JSONException {
        JSONObject object = new JSONObject(buffer);
        mRefreshToken = object.getString("refresh_token");
        return mRefreshToken;
    }
}
