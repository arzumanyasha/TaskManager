package com.example.arturarzumanyan.taskmanager.auth;

import android.net.Uri;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.Constants;

import org.json.JSONException;

public class RefreshTokenAsyncTask extends AsyncTask<String, Void, String> {

    private TokenAsyncTaskEvents mTokenAsyncTaskEvents;
    private String mAccessToken;

    public RefreshTokenAsyncTask(TokenAsyncTaskEvents tokenAsyncTaskEvents) {
        mTokenAsyncTaskEvents = tokenAsyncTaskEvents;
    }

    @Override
    protected String doInBackground(String... strings) {
        String mRefreshToken = strings[0];
        TokenHttpUrlConnection tokenHttpUrlConnection = new TokenHttpUrlConnection();
        mAccessToken = tokenHttpUrlConnection.getAccessToken(mRefreshToken, Constants.REFRESH_TOKEN_KEY);
        return mAccessToken;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mTokenAsyncTaskEvents != null) {
            try {
                mTokenAsyncTaskEvents.onPostExecute(mAccessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
