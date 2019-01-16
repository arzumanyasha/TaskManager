package com.example.arturarzumanyan.taskmanager.domain;

public class ResponseDto {
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
}
