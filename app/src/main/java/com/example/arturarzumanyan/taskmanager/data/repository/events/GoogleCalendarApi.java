package com.example.arturarzumanyan.taskmanager.data.repository.events;

import java.util.Map;

import io.reactivex.Single;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface GoogleCalendarApi {
    @GET
    Single<ResponseBody> getEvents(@Url String url, @HeaderMap Map<String, String> map);

    @POST
    Single<ResponseBody> addEvent(@Url String url, @HeaderMap Map<String, String> map, @Body Map<String, Object> requestBody);

    @PATCH
    Single<ResponseBody> updateEvent(@Url String url, @HeaderMap Map<String, String> map, @Body Map<String, Object> requestBody);

    @DELETE
    Single<Response<ResponseBody>> deleteEvent(@Url String url, @HeaderMap Map<String, String> map);
}
