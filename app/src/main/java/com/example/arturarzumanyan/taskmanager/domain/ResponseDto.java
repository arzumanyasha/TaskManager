package com.example.arturarzumanyan.taskmanager.domain;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class ResponseDto extends Observable<ResponseDto> {
    private final int responseCode;
    private final String responseData;

    public ResponseDto(int responseCode, String responseData) {
        this.responseCode = responseCode;
        this.responseData = responseData;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    @Override
    protected void subscribeActual(Observer<? super ResponseDto> observer) {

    }
}
