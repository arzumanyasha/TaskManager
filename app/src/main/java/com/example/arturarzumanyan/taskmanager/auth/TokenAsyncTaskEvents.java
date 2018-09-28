package com.example.arturarzumanyan.taskmanager.auth;

import org.json.JSONException;

public interface TokenAsyncTaskEvents {
    void onPostExecute(String accessToken) throws JSONException;
}
