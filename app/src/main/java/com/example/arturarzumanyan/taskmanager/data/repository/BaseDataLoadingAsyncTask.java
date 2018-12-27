package com.example.arturarzumanyan.taskmanager.data.repository;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public abstract class BaseDataLoadingAsyncTask<T> extends AsyncTask<FirebaseWebService.RequestMethods, Void, List<T>> {

    public BaseDataLoadingAsyncTask() {
    }

    @Override
    protected List<T> doInBackground(FirebaseWebService.RequestMethods... requestMethods) {
        return null;
    }

    @Override
    protected void onPostExecute(List<T> data) {
        super.onPostExecute(data);
        userDataLoadingListener.onSuccess(data);
    }

    public interface UserDataLoadingListener<T> {
        void onSuccess(List<T> list);
    }

    public void setDataInfoLoadingListener(UserDataLoadingListener<T> listener) {
        this.userDataLoadingListener = listener;
    }

    private UserDataLoadingListener<T> userDataLoadingListener;
}
