package com.example.arturarzumanyan.taskmanager.networking.util;

import android.support.annotation.Nullable;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.TOKEN_TYPE;

public class GoogleAuthenticator implements Authenticator {
    @Nullable
    @Override
    public synchronized Request authenticate(@Nullable Route route, Response response) throws IOException {
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {

            Log.v("REFRESHING TOKEN IN " + Thread.currentThread());

            if (response.request().header(AUTHORIZATION_KEY) == null) {
                return null;
            }

            ResponseBody responseBody = FirebaseWebService.getFirebaseWebServiceInstance().refreshAccessToken().body();
            if (responseBody != null) {
                String accessToken = FirebaseWebService.getFirebaseWebServiceInstance().getAccessTokenFromBuffer(responseBody.string());
                TokenStorage.getTokenStorageInstance().writeAccessToken(accessToken);
            } else {
                return null;
            }
        }

        return response.request().newBuilder()
                .header(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken())
                .build();
    }
}
