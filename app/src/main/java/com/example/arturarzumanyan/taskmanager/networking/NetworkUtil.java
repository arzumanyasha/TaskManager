package com.example.arturarzumanyan.taskmanager.networking;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import java.util.HashMap;

public class NetworkUtil {
    public static ResponseDto getResultFromServer(RequestParameters requestParameters) {
        String url = requestParameters.getUrl();
        FirebaseWebService.RequestMethods requestMethod = requestParameters.getRequestMethod();
        HashMap<String, Object> requestBodyParameters = requestParameters.getRequestBodyParameters();
        HashMap<String, String> requestHeaderParameters = requestParameters.getRequestHeaderParameters();

        BaseHttpUrlConnection baseHttpUrlConnection = new BaseHttpUrlConnection();
        return baseHttpUrlConnection.getResult(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
    }
}
