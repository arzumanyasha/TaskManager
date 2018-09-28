package com.example.arturarzumanyan.taskmanager.networking;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;

import java.util.HashMap;

public class RequestParameters {
    private String url;
    private FirebaseWebService.RequestMethods requestMethod;
    private HashMap<String, String> requestBodyParameters;
    private HashMap<String, String> requestHeaderParameters;

    public RequestParameters(String url,
                             FirebaseWebService.RequestMethods requestMethod,
                             HashMap<String, String> requestBodyParameters,
                             HashMap<String, String> requestHeaderParameters) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.requestBodyParameters = requestBodyParameters;
        this.requestHeaderParameters = requestHeaderParameters;
    }

    public String getUrl() {
        return url;
    }

    public FirebaseWebService.RequestMethods getRequestMethod() {
        return requestMethod;
    }

    public HashMap<String, String> getRequestBodyParameters() {
        return requestBodyParameters;
    }

    public HashMap<String, String> getRequestHeaderParameters() {
        return requestHeaderParameters;
    }
}
