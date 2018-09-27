package com.example.arturarzumanyan.taskmanager.networking;

import java.util.HashMap;

public class RequestParameters {
    private String url;
    private String requestMethod;
    private HashMap<String, String> requestBodyParameters;
    private HashMap<String, String> requestHeaderParameters;

    public RequestParameters(String url,
                             String requestMethod,
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

    public String getRequestMethod() {
        return requestMethod;
    }

    public HashMap<String, String> getRequestBodyParameters() {
        return requestBodyParameters;
    }

    public HashMap<String, String> getRequestHeaderParameters() {
        return requestHeaderParameters;
    }
}
