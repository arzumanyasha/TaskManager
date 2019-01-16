package com.example.arturarzumanyan.taskmanager.auth;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.networking.NetworkUtil;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import org.json.JSONException;

public class AccessTokenAsyncTask extends AsyncTask<RequestParameters, Void, ResponseDto> {
    private TokensLoadingListener tokensLoadingListener;

    public AccessTokenAsyncTask() {
    }

    @Override
    protected ResponseDto doInBackground(RequestParameters... requestParameters) {
        return NetworkUtil.getResultFromServer(requestParameters[0]);
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
}
