package com.example.arturarzumanyan.taskmanager.auth;

import android.os.AsyncTask;

import org.json.JSONException;

public class AccessTokenAsyncTask extends AsyncTask<String, Void, String> {

    private TokenAsyncTaskEvents mTokenAsyncTaskEvents;
    private String mBuffer;

    public AccessTokenAsyncTask(TokenAsyncTaskEvents tokenAsyncTaskEvents) {
        mTokenAsyncTaskEvents = tokenAsyncTaskEvents;
    }

    @Override
    protected String doInBackground(String... strings) {
        String authCode = strings[0];
        String authCodeKey = strings[1];
        String grantType = strings[2];

        TokenHttpUrlConnection tokenHttpUrlConnection = new TokenHttpUrlConnection();
        mBuffer = tokenHttpUrlConnection.getAccessToken(authCode, authCodeKey, grantType);
        return mBuffer;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mTokenAsyncTaskEvents != null) {
            try {
                mTokenAsyncTaskEvents.onPostExecute(mBuffer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
