package com.example.arturarzumanyan.taskmanager.data.repository;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public abstract class BaseDataLoadingAsyncTask<T> extends AsyncTask<RequestParameters, Void, List<T>> {

    public BaseDataLoadingAsyncTask() {
    }

    @Override
    protected List<T> doInBackground(RequestParameters... requestParameters) {
        return null;
    }

    public String getResultFromServer(RequestParameters requestParameters) {
        String url = requestParameters.getUrl();
        FirebaseWebService.RequestMethods requestMethod = requestParameters.getRequestMethod();
        HashMap<String, Object> requestBodyParameters = requestParameters.getRequestBodyParameters();
        HashMap<String, String> requestHeaderParameters = requestParameters.getRequestHeaderParameters();

        BaseHttpUrlConnection baseHttpUrlConnection = new BaseHttpUrlConnection();
        return baseHttpUrlConnection.getResult(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
    }

    @Override
    protected void onPostExecute(List<T> data) {
        super.onPostExecute(data);
        if (data.size() != 0) {
            userDataLoadingListener.onSuccess(data);
        } else {
            userDataLoadingListener.onFail();
        }
    }

    public interface UserDataLoadingListener<T> {
        void onSuccess(List<T> list);

        void onFail();
    }

    public void setDataInfoLoadingListener(UserDataLoadingListener<T> listener) {
        this.userDataLoadingListener = listener;
    }

    private UserDataLoadingListener<T> userDataLoadingListener;
}
