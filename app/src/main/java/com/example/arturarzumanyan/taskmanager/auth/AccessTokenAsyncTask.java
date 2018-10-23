package com.example.arturarzumanyan.taskmanager.auth;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;

import org.json.JSONException;

import java.util.HashMap;

public class AccessTokenAsyncTask extends AsyncTask<RequestParameters, Void, String> {

    private String mBuffer;

    public AccessTokenAsyncTask() {
        tokensLoadingListener = null;
    }

    @Override
    protected String doInBackground(RequestParameters... requestParameters) {
        String url = requestParameters[0].getUrl();
        FirebaseWebService.RequestMethods requestMethod = requestParameters[0].getRequestMethod();
        HashMap<String, Object> requestBodyParameters = requestParameters[0].getRequestBodyParameters();
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

        if (tokensLoadingListener != null) {
            try {
                tokensLoadingListener.onDataLoaded(mBuffer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface TokensLoadingListener {
        void onDataLoaded(String buffer) throws JSONException;
    }

    public void setTokensLoadingListener(TokensLoadingListener listener) {
        this.tokensLoadingListener = listener;
    }

    private TokensLoadingListener tokensLoadingListener;
}
