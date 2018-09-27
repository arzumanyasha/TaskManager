package com.example.arturarzumanyan.taskmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.auth.AccessTokenAsyncTask;
import com.example.arturarzumanyan.taskmanager.auth.TokenAsyncTaskEvents;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.networking.RequestParameters;
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

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements TokenAsyncTaskEvents {

    private static final String BASE_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static final String CLIENT_ID = "685238908043-obre149i2k2gh9a71g2it0emsa97glma.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "6ygf5qYHRMx3AnIwXGbLhWuz";
    private static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    private static final String TASKS_SCOPE = "https://www.googleapis.com/auth/tasks";
    private static final int REQUEST_CODE = 101;
    public static final String EXTRA_USER_NAME = "userName";
    public static final String EXTRA_USER_EMAIL = "userEmail";
    public static final String EXTRA_USER_PHOTO_URL = "userPhotoUrl";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

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
                .requestIdToken(CLIENT_ID)
                .requestServerAuthCode(CLIENT_ID)
                .requestEmail()
                .requestScopes(new Scope(CALENDAR_SCOPE),
                               new Scope(TASKS_SCOPE))
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
            updateUI(currentUser.getDisplayName(),
                     currentUser.getEmail(),
                     currentUser.getPhotoUrl().toString());

        }
    }

    private void updateUI(String userName, String userEmail, String userPhotoUrl) {
        Toast.makeText(getApplicationContext(), getString(R.string.logged_in_message), Toast.LENGTH_LONG).show();

        Intent accountIntent = new Intent(SignInActivity.this, IntentionActivity.class);
        accountIntent.putExtra(EXTRA_USER_NAME, userName);
        accountIntent.putExtra(EXTRA_USER_EMAIL, userEmail);
        accountIntent.putExtra(EXTRA_USER_PHOTO_URL, userPhotoUrl);
        startActivity(accountIntent);
        finish();
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                firebaseAuthWithGoogle(acct);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        String authCode = acct.getServerAuthCode();
        mAccessTokenAsyncTask = new AccessTokenAsyncTask(this);

        String requestType = "POST";
        HashMap<String, String> requestBodyParameters = new HashMap<>();
        requestBodyParameters.put("code", authCode);
        requestBodyParameters.put("client_id", CLIENT_ID);
        requestBodyParameters.put("client_secret", CLIENT_SECRET);
        requestBodyParameters.put("grant_type", "authorization_code");
        HashMap<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put("Content-Type", "application/x-www-form-urlencoded");

        RequestParameters requestParameters = new RequestParameters(BASE_URL,
                requestType,
                requestBodyParameters,
                requestHeaderParameters);

        mAccessTokenAsyncTask.execute(requestParameters);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user.getDisplayName(),
                                     user.getEmail(),
                                     user.getPhotoUrl().toString());
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

        TokenStorage tokenStorage = new TokenStorage();
        tokenStorage.write(SignInActivity.this, accessToken, refreshToken);
        Toast.makeText(SignInActivity.this, accessToken, Toast.LENGTH_SHORT).show();
    }

    private String getAccessTokenFromBuffer(String buffer) throws JSONException {
        JSONObject object = new JSONObject(buffer);
        mAccessToken = object.getString(ACCESS_TOKEN_KEY);
        return mAccessToken;
    }

    private String getRefreshTokenFromBuffer(String buffer) throws JSONException {
        JSONObject object = new JSONObject(buffer);
        mRefreshToken = object.getString(REFRESH_TOKEN_KEY);
        return mRefreshToken;
    }
}
