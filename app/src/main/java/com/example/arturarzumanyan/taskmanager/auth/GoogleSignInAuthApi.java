package com.example.arturarzumanyan.taskmanager.auth;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface GoogleSignInAuthApi {

    @POST("oauth2/v4/token/")
    Single<ResponseBody> requestToken(@Body RequestBody requestBody, @HeaderMap Map<String, String> headerMap);

    @POST("oauth2/v4/token/")
    Single<ResponseBody> requestRefreshToken(@Body RequestBody requestBody, @HeaderMap Map<String, String> headerMap);
}
