package com.example.arturarzumanyan.taskmanager.networking;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;

import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.TOKEN_TYPE;

public class GoogleSuiteApiFactory {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://www.googleapis.com/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .authenticator((route, response) -> {
                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            Log.v("REFRESHING TOKEN");
                            ResponseBody responseBody = FirebaseWebService.getFirebaseWebServiceInstance().refreshAccessToken().body();
                            String accessToken = null;
                            if (responseBody != null) {
                                accessToken = FirebaseWebService.getFirebaseWebServiceInstance().getAccessTokenFromBuffer(responseBody.string());
                            }
                            TokenStorage.getTokenStorageInstance().writeAccessToken(accessToken);
                        }

                        return response.request().newBuilder()
                                .header(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken())
                                .build();
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();
        }

        return retrofit;
    }
}
