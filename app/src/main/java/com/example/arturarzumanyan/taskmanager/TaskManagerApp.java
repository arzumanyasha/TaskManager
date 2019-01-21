package com.example.arturarzumanyan.taskmanager;

import android.app.Application;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;

public class TaskManagerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TokenStorage.initTokenStorageInstance(getApplicationContext());
        FirebaseWebService.initFirebaseWebServiceInstance(getApplicationContext());
        DbHelper.initDbHelperInstance(getApplicationContext());
    }
}
