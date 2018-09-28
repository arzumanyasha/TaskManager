package com.example.arturarzumanyan.taskmanager.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.networking.RequestParameters;
import com.example.arturarzumanyan.taskmanager.ui.SignInActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
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

public class FirebaseWebService implements GoogleApiClient.OnConnectionFailedListener, TokenAsyncTaskEvents, OnCompleteListener{

    private static final String BASE_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static final String CLIENT_ID = "685238908043-obre149i2k2gh9a71g2it0emsa97glma.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "6ygf5qYHRMx3AnIwXGbLhWuz";
    private static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    private static final String TASKS_SCOPE = "https://www.googleapis.com/auth/tasks";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mApiClient;
    private AccessTokenAsyncTask mAccessTokenAsyncTask;
    private String mAccessToken;
    private String mRefreshToken;
    private Context mContext;

    private UpdateUiCallback mUpdateUiCallback = null;

    public FirebaseWebService(UpdateUiCallback updateUiCallback){
        this.mUpdateUiCallback = updateUiCallback;
    }

    public void setGoogleClient(Context context){
        mContext = context;

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestServerAuthCode(CLIENT_ID)
                .requestEmail()
                .requestScopes(new Scope(CALENDAR_SCOPE),
                        new Scope(TASKS_SCOPE))
                .build();

        mApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity)context,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void firebaseAuthWithGoogle(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String authCode = acct.getServerAuthCode();
            requestToken(authCode);

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this);

        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            FirebaseUser user = mAuth.getCurrentUser();
            mUpdateUiCallback.updateUi(user.getDisplayName(),
                    user.getEmail(),
                    user.getPhotoUrl().toString());

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPostExecute(String buffer) throws JSONException {
        String accessToken = getAccessTokenFromBuffer(buffer);
        String refreshToken = getRefreshTokenFromBuffer(buffer);

        TokenStorage tokenStorage = new TokenStorage();
        tokenStorage.write(mContext, accessToken, refreshToken);
        Toast.makeText(mContext, accessToken, Toast.LENGTH_SHORT).show();
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

    private void requestToken(String authCode){
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
    }

    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public Intent getGoogleSignInClientIntent(){
        return mGoogleSignInClient.getSignInIntent();
    }

    public void destroyAsyncTask(){
        if(mAccessTokenAsyncTask != null){
            mAccessTokenAsyncTask.cancel(false);
            mAccessTokenAsyncTask = null;
        }
    }
}
