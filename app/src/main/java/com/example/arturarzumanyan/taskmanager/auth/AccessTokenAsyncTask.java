package com.example.arturarzumanyan.taskmanager.auth;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.networking.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.BaseHttpUrlConnection;

import org.json.JSONException;

import java.util.HashMap;

public class AccessTokenAsyncTask extends AsyncTask<RequestParameters, Void, String> {

    private TokenAsyncTaskEvents mTokenAsyncTaskEvents;
    private String mBuffer;

    public AccessTokenAsyncTask(TokenAsyncTaskEvents tokenAsyncTaskEvents) {
        mTokenAsyncTaskEvents = tokenAsyncTaskEvents;
    }

    @Override
    protected String doInBackground(RequestParameters... requestParameters) {
        String url = requestParameters[0].getUrl();
        FirebaseWebService.RequestMethods requestMethod = requestParameters[0].getRequestMethod();
        HashMap<String, String> requestBodyParameters = requestParameters[0].getRequestBodyParameters();
        HashMap<String, String> requestHeaderParameters = requestParameters[0].getRequestHeaderParameters();

        BaseHttpUrlConnection baseHttpUrlConnection = new BaseHttpUrlConnection();
        mBuffer = baseHttpUrlConnection.getResult(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
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
