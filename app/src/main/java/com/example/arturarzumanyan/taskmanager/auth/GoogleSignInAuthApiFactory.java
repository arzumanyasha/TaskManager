package com.example.arturarzumanyan.taskmanager.auth;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class GoogleSignInAuthApiFactory {
    private static GoogleSignInAuthApi googleSignInAuthApi;
    private static final String BASE_URL = "https://www.googleapis.com/";

    public static GoogleSignInAuthApi getSignInService() {
        if (googleSignInAuthApi == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            googleSignInAuthApi = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build()
                    .create(GoogleSignInAuthApi.class);
        }

        return googleSignInAuthApi;
    }
}
