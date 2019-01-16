package com.example.arturarzumanyan.taskmanager.networking.base;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;

import java.util.Map;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;

public class RequestParameters {
    private String url;
    private FirebaseWebService.RequestMethods requestMethod;
    private Map<String, Object> requestBodyParameters;
    private Map<String, String> requestHeaderParameters;

    public RequestParameters(String url,
                             FirebaseWebService.RequestMethods requestMethod,
                             Map<String, Object> requestBodyParameters,
                             Map<String, String> requestHeaderParameters) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.requestBodyParameters = requestBodyParameters;
        this.requestHeaderParameters = requestHeaderParameters;
    }

    public RequestParameters(
            String url,
            FirebaseWebService.RequestMethods requestMethod,
            Map<String, Object> requestBodyParameters) {
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

    public Map<String, Object> getRequestBodyParameters() {
        return requestBodyParameters;
    }

    public Map<String, String> getRequestHeaderParameters() {
        return requestHeaderParameters;
    }

    public void setRequestBodyParameters(Map<String, Object> requestBodyParameters) {

        this.requestBodyParameters = requestBodyParameters;
    }

    public void setRequestHeaderParameters(Map<String, String> requestHeaderParameters) {
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + TokenStorage.getTokenStorageInstance().getAccessToken());

        this.requestHeaderParameters = requestHeaderParameters;
    }
}
