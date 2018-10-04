package com.example.arturarzumanyan.taskmanager.networking;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;

public class UserDataAsyncTask extends AsyncTask<RequestParameters, Void, String> {

    private String mBuffer;

    public UserDataAsyncTask() {
        this.userDataLoadingListener = null;
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
        if (userDataLoadingListener != null) {
            try {
                userDataLoadingListener.onDataLoaded(mBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface UserDataLoadingListener {
        void onDataLoaded(String response) throws JSONException, ParseException;
    }

    public void setDataInfoLoadingListener(UserDataLoadingListener listener) {
        this.userDataLoadingListener = listener;
    }

    private UserDataLoadingListener userDataLoadingListener;
}
