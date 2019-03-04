package com.example.arturarzumanyan.taskmanager.networking;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;

import java.util.Map;

import io.reactivex.Observable;

public class NetworkUtil {
    public static Observable<ResponseDto> getResultFromServer(RequestParameters requestParameters) {
        String url = requestParameters.getUrl();
        FirebaseWebService.RequestMethods requestMethod = requestParameters.getRequestMethod();
        Map<String, Object> requestBodyParameters = requestParameters.getRequestBodyParameters();
        Map<String, String> requestHeaderParameters = requestParameters.getRequestHeaderParameters();

        Log.v("NETWORKING REQUEST " + requestParameters.getUrl());
        Log.v("NETWORKING REQUEST " + requestParameters.getRequestMethod().toString());
        Log.v("NETWORKING REQUEST " + requestParameters.getRequestHeaderParameters().toString());
        if(requestBodyParameters != null) {
            Log.v("NETWORKING REQUEST " + requestParameters.getRequestBodyParameters().toString());
        }
        BaseHttpUrlConnection baseHttpUrlConnection = new BaseHttpUrlConnection();
        return baseHttpUrlConnection.getResult(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
    }
}
