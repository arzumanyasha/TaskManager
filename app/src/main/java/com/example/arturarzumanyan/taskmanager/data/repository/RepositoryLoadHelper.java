package com.example.arturarzumanyan.taskmanager.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import java.io.IOException;
import java.util.HashMap;

public class RepositoryLoadHelper {
    public static final String AUTHORIZATION_KEY = "Authorization";

    private Context mContext;

    public RepositoryLoadHelper(Context context) {
        this.mContext = context;
    }

    public void requestUserData(UserDataAsyncTask asyncTask, String url) {
        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                new HashMap<String, String>()
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
