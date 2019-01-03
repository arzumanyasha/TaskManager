package com.example.arturarzumanyan.taskmanager.auth;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;

import org.json.JSONException;

import java.util.HashMap;

public class AccessTokenAsyncTask extends AsyncTask<RequestParameters, Void, ResponseDto> {

    public AccessTokenAsyncTask() {
        tokensLoadingListener = null;
    }

    @Override
    protected ResponseDto doInBackground(RequestParameters... requestParameters) {
        String url = requestParameters[0].getUrl();
        FirebaseWebService.RequestMethods requestMethod = requestParameters[0].getRequestMethod();
        HashMap<String, Object> requestBodyParameters = requestParameters[0].getRequestBodyParameters();
        HashMap<String, String> requestHeaderParameters = requestParameters[0].getRequestHeaderParameters();

        BaseHttpUrlConnection baseHttpUrlConnection = new BaseHttpUrlConnection();
        return baseHttpUrlConnection.getResult(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
    }

    @Override
    protected void onPostExecute(ResponseDto responseDto) {
        super.onPostExecute(responseDto);

        if (tokensLoadingListener != null) {
            try {
                tokensLoadingListener.onDataLoaded(responseDto.getResponseData());
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
