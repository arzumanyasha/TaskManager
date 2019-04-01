package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

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

public interface GoogleTasksApi {
    @GET
    Single<ResponseBody> getTasksFromTaskList(@Url String url);

    @POST
    Single<ResponseBody> addTask(@Url String url, @HeaderMap Map<String, String> map, @Body Map<String, Object> requestBody);

    @PATCH
    Single<ResponseBody> updateTask(@Url String url, @HeaderMap Map<String, String> map, @Body Map<String, Object> requestBody);

    @DELETE
    Single<Response<ResponseBody>> deleteTask(@Url String url);

    @GET
    Single<ResponseBody> getTaskLists(@Url String url);

    @POST
    Single<ResponseBody> addTaskList(@Url String url, @HeaderMap Map<String, String> map, @Body Map<String, Object> requestBody);

    @PATCH
    Single<ResponseBody> updateTaskList(@Url String url, @HeaderMap Map<String, String> map, @Body Map<String, Object> requestBody);

    @DELETE
    Single<Response<ResponseBody>> deleteTaskList(@Url String url);
}
