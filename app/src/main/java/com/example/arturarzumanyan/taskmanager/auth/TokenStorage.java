package com.example.arturarzumanyan.taskmanager.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.arturarzumanyan.taskmanager.ui.SignInActivity;

import static android.content.Context.MODE_PRIVATE;

public class TokenStorage {
    private static final String SHARED_PREFS_NAME = "sharedPrefs";
    private static final String DEFAULT = "";

    public void write(Context context, String accessToken, String refreshToken){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FirebaseWebService.ACCESS_TOKEN_KEY, accessToken);
        editor.putString(FirebaseWebService.REFRESH_TOKEN_KEY, refreshToken);
        editor.apply();
    }

    public void writeAccessToken(Context context, String accessToken){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FirebaseWebService.ACCESS_TOKEN_KEY, accessToken);
        editor.apply();
    }

    public String getAccessToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(FirebaseWebService.ACCESS_TOKEN_KEY, DEFAULT);
    }

    public String getRefreshToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(FirebaseWebService.REFRESH_TOKEN_KEY, DEFAULT);
    }

}
