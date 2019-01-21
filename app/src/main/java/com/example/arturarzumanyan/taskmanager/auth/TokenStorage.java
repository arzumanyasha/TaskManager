package com.example.arturarzumanyan.taskmanager.auth;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class TokenStorage {
    private static final String SHARED_PREFS_NAME = "sharedPrefs";
    private static final String DEFAULT = "";
    private Context mContext;

    private static TokenStorage mTokenStorageInstance;

    public static void initTokenStorageInstance(Context context) {
        if (mTokenStorageInstance == null) {
            mTokenStorageInstance = new TokenStorage(context);
        }
    }

    public synchronized static TokenStorage getTokenStorageInstance() {
        return mTokenStorageInstance;
    }

    private TokenStorage(Context context) {
        this.mContext = context;
    }

    public void write(String accessToken, String refreshToken) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FirebaseWebService.ACCESS_TOKEN_KEY, accessToken);
        editor.putString(FirebaseWebService.REFRESH_TOKEN_KEY, refreshToken);
        editor.apply();
    }

    public void writeAccessToken(String accessToken) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FirebaseWebService.ACCESS_TOKEN_KEY, accessToken);
        editor.apply();
    }

    public String getAccessToken() {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(FirebaseWebService.ACCESS_TOKEN_KEY, DEFAULT);
    }

    public String getRefreshToken() {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(FirebaseWebService.REFRESH_TOKEN_KEY, DEFAULT);
    }

}
