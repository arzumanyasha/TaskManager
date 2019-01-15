package com.example.arturarzumanyan.taskmanager.auth;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseWebService implements GoogleApiClient.OnConnectionFailedListener, OnCompleteListener {

    private static final String BASE_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static final String CLIENT_ID = "685238908043-obre149i2k2gh9a71g2it0emsa97glma.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "6ygf5qYHRMx3AnIwXGbLhWuz";
    private static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    private static final String TASKS_SCOPE = "https://www.googleapis.com/auth/tasks";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    public enum RequestMethods {POST, GET, PATCH, DELETE}

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mApiClient;
    private AccessTokenAsyncTask mAccessTokenAsyncTask;
    private Context mContext;
    private TokenStorage mTokenStorage = new TokenStorage();

    public FirebaseWebService(Context context) {
        this.userInfoLoadingListener = null;
        this.mContext = context;
    }

    public void setGoogleClientOptions() {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestServerAuthCode(CLIENT_ID)
                .requestEmail()
                .requestScopes(new Scope(CALENDAR_SCOPE),
                        new Scope(TASKS_SCOPE))
                .build();

        mApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mContext, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
    }

    public void authWithGoogle(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String authCode;
            if (acct != null) {
                authCode = acct.getServerAuthCode();
                requestToken(authCode);

                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this);
            } else {
                userInfoLoadingListener.onFail();
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (userInfoLoadingListener != null) {
                if (user != null) {
                    userInfoLoadingListener.onDataLoaded(user.getDisplayName(),
                            user.getEmail(),
                            String.valueOf(user.getPhotoUrl()));
                } else {
                    userInfoLoadingListener.onFail();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String getAccessTokenFromBuffer(String buffer) throws JSONException {
        JSONObject object = new JSONObject(buffer);
        return object.getString(ACCESS_TOKEN_KEY);
    }

    private String getRefreshTokenFromBuffer(String buffer) throws JSONException {
        JSONObject object = new JSONObject(buffer);
        return object.getString(REFRESH_TOKEN_KEY);
    }

    private void requestToken(String authCode) {
        mAccessTokenAsyncTask = new AccessTokenAsyncTask();
        mAccessTokenAsyncTask.setTokensLoadingListener(new AccessTokenAsyncTask.TokensLoadingListener() {
            @Override
            public void onDataLoaded(String buffer) throws JSONException {
                String accessToken = getAccessTokenFromBuffer(buffer);
                String refreshToken = getRefreshTokenFromBuffer(buffer);

                mTokenStorage.write(mContext, accessToken, refreshToken);
            }
        });

        RequestParameters requestParameters = getAccessTokenParameters(authCode);
        mAccessTokenAsyncTask.execute(requestParameters);
    }

    private RequestParameters getAccessTokenParameters(String authCode) {
        RequestMethods requestMethod = RequestMethods.POST;
        Map<String, Object> requestBodyParameters = new HashMap<>();
        requestBodyParameters.put("code", authCode);
        requestBodyParameters.put(CLIENT_ID_KEY, CLIENT_ID);
        requestBodyParameters.put(CLIENT_SECRET_KEY, CLIENT_SECRET);
        requestBodyParameters.put(GRANT_TYPE_KEY, "authorization_code");
        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(CONTENT_TYPE_KEY, CONTENT_TYPE);

        return new RequestParameters(BASE_URL,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);

    }

    public void refreshAccessToken(final AccessTokenUpdatedListener listener) {
        AccessTokenAsyncTask accessTokenAsyncTask = new AccessTokenAsyncTask();
        accessTokenAsyncTask.setTokensLoadingListener(new AccessTokenAsyncTask.TokensLoadingListener() {
            @Override
            public void onDataLoaded(String buffer) throws JSONException {
                String accessToken = getAccessTokenFromBuffer(buffer);

                mTokenStorage.writeAccessToken(mContext, accessToken);
                listener.onAccessTokenUpdated();
            }
        });

        RequestParameters requestParameters = getRefreshTokenParameters();
        accessTokenAsyncTask.execute(requestParameters);
    }

    private RequestParameters getRefreshTokenParameters() {
        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.POST;
        Map<String, Object> requestBodyParameters = new HashMap<>();
        requestBodyParameters.put(REFRESH_TOKEN_KEY, mTokenStorage.getRefreshToken(mContext));
        requestBodyParameters.put(CLIENT_ID_KEY, CLIENT_ID);
        requestBodyParameters.put(CLIENT_SECRET_KEY, CLIENT_SECRET);
        requestBodyParameters.put(GRANT_TYPE_KEY, REFRESH_TOKEN_KEY);
        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(CONTENT_TYPE_KEY, CONTENT_TYPE);

        return new RequestParameters(BASE_URL,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
    }

    public FirebaseUser getCurrentUser() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    public String getUserEmail() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.getEmail();
        } else {
            return null;
        }
    }

    public Intent getGoogleSignInClientIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }

    public void closeAuthConnection() {
        if (mAccessTokenAsyncTask != null) {
            mAccessTokenAsyncTask.cancel(false);
            mAccessTokenAsyncTask = null;
        }
    }

    public interface UserInfoLoadingListener {
        void onDataLoaded(String userName, String userEmail, String userPhotoUrl);

        void onFail();
    }

    public void setUserInfoLoadingListener(UserInfoLoadingListener listener) {
        this.userInfoLoadingListener = listener;
    }

    private UserInfoLoadingListener userInfoLoadingListener;

    public interface AccessTokenUpdatedListener {
        void onAccessTokenUpdated();
    }

    public void setAccessTokenUpdatedListener(AccessTokenUpdatedListener listener) {
        this.accessTokenUpdatedListener = listener;
    }

    private AccessTokenUpdatedListener accessTokenUpdatedListener;

}
