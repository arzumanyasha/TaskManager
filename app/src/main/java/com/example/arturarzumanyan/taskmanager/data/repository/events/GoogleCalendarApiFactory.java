package com.example.arturarzumanyan.taskmanager.data.repository.events;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class GoogleCalendarApiFactory {
    private static GoogleCalendarApi googleCalendarApi;
    private static final String BASE_URL = "https://www.googleapis.com/";

    public static GoogleCalendarApi getGoogleCalendarApi() {
        if (googleCalendarApi == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new RepositoryLoadHelper.TokenRefresherInterceptor())
                    .build();

            googleCalendarApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build()
                    .create(GoogleCalendarApi.class);
        }

        return googleCalendarApi;
    }
}
