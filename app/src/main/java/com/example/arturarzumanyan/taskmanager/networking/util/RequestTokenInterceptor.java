package com.example.arturarzumanyan.taskmanager.networking.util;

import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.TOKEN_TYPE;

public class RequestTokenInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest;

        newRequest = request.newBuilder()
                .addHeader(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken())
                .build();
        return chain.proceed(newRequest);
    }
}
