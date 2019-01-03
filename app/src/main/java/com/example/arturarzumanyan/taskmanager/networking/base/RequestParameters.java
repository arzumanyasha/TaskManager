package com.example.arturarzumanyan.taskmanager.networking.base;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;

import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;

public class RequestParameters {
    private Context mContext;
    private String url;
    private FirebaseWebService.RequestMethods requestMethod;
    private HashMap<String, Object> requestBodyParameters;
    private HashMap<String, String> requestHeaderParameters;

    public RequestParameters(String url,
                             FirebaseWebService.RequestMethods requestMethod,
                             HashMap<String, Object> requestBodyParameters,
                             HashMap<String, String> requestHeaderParameters) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.requestBodyParameters = requestBodyParameters;
        this.requestHeaderParameters = requestHeaderParameters;
    }

    public RequestParameters(Context mContext,
                             String url,
                             FirebaseWebService.RequestMethods requestMethod,
                             HashMap<String, Object> requestBodyParameters) {
        this.mContext = mContext;
        this.url = url;
        this.requestMethod = requestMethod;
        this.requestBodyParameters = requestBodyParameters;
    }

    public String getUrl() {
        return url;
    }

    public FirebaseWebService.RequestMethods getRequestMethod() {
        return requestMethod;
    }

    public HashMap<String, Object> getRequestBodyParameters() {
        return requestBodyParameters;
    }

    public HashMap<String, String> getRequestHeaderParameters() {
        return requestHeaderParameters;
    }

    public void setRequestBodyParameters(HashMap<String, Object> requestBodyParameters) {

        this.requestBodyParameters = requestBodyParameters;
    }

    public void setRequestHeaderParameters(HashMap<String, String> requestHeaderParameters) {
        TokenStorage tokenStorage = new TokenStorage();
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + tokenStorage.getAccessToken(mContext));

        this.requestHeaderParameters = requestHeaderParameters;
    }
}
