package com.example.arturarzumanyan.taskmanager;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class TaskManagerApp extends MultiDexApplication {
    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        TokenStorage.initTokenStorageInstance(this);
        FirebaseWebService.initFirebaseWebServiceInstance(this);
        DbHelper.initDbHelperInstance(this);
        ResourceManager.initResourceManager(this);
        installLeakCanary();
    }

    public static RefWatcher getRefWatcher(Context context) {
        TaskManagerApp application = (TaskManagerApp) context.getApplicationContext();
        return application.mRefWatcher;
    }

    private void installLeakCanary() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            mRefWatcher = LeakCanary.install(this);
        }
    }
}
