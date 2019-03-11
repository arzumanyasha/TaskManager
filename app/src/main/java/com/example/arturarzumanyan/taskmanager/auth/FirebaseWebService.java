package com.example.arturarzumanyan.taskmanager.auth;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.domain.User;
import com.example.arturarzumanyan.taskmanager.networking.NetworkUtil;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FirebaseWebService {

    private static final String BASE_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static final String CLIENT_ID = "685238908043-obre149i2k2gh9a71g2it0emsa97glma.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "6ygf5qYHRMx3AnIwXGbLhWuz";
    private static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.events";
    private static final String TASKS_SCOPE = "https://www.googleapis.com/auth/tasks";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String CODE_KEY = "code";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    public enum RequestMethods {POST, GET, PATCH, DELETE}

    private static FirebaseWebService mFirebaseWebServiceInstance;
    private GoogleSignInClient mGoogleSignInClient;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Context mContext;

    public static void initFirebaseWebServiceInstance(Context context) {
        if (mFirebaseWebServiceInstance == null) {
            mFirebaseWebServiceInstance = new FirebaseWebService(context);
        }
    }

    public synchronized static FirebaseWebService getFirebaseWebServiceInstance() {
        return mFirebaseWebServiceInstance;
    }

    private FirebaseWebService(Context context) {
        this.mContext = context;
    }

    public void setGoogleClientOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestServerAuthCode(CLIENT_ID, true)
                .requestEmail()
                .requestScopes(new Scope(CALENDAR_SCOPE),
                        new Scope(TASKS_SCOPE))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
    }

    public Single<User> authWithGoogle(Intent data) {

        return getSignInResultFromIntent(data)
                .filter(googleSignInResult -> googleSignInResult != null)
                .map(GoogleSignInResult::getSignInAccount)
                .filter(googleSignInAccount -> googleSignInAccount != null)
                .doOnSuccess(googleSignInAccount -> requestToken(googleSignInAccount.getServerAuthCode()))
                .map(googleSignInAccount -> GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null))
                .flatMapSingle((Function<AuthCredential, Single<User>>) authCredential -> signInWithCredential(FirebaseAuth.getInstance(), authCredential)
                        .map(authResult -> new User(authResult.getUser().getDisplayName(),
                                authResult.getUser().getEmail(),
                                String.valueOf(authResult.getUser().getPhotoUrl()))));
    }

    private static Single<GoogleSignInResult> getSignInResultFromIntent(final Intent data) {

        return Single.fromCallable(() -> Auth.GoogleSignInApi.getSignInResultFromIntent(data));
    }

    private static Single<AuthResult> signInWithCredential(@NonNull final FirebaseAuth firebaseAuth,
                                                           @NonNull final AuthCredential credential) {
        return Single.create(e -> RxTask.assignOnTask(e, firebaseAuth.signInWithCredential(credential)));
    }

    private void requestToken(String authCode) {
        Log.v("REQUESTING TOKEN " + authCode);
        RequestParameters requestParameters = getAccessTokenParameters(authCode);
        mCompositeDisposable.add(NetworkUtil.getResultFromServer(requestParameters)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(responseDto -> {
                    Log.v("ACCESS TOKEN RECEIVED");
                    String accessToken = getAccessTokenFromBuffer(responseDto.getResponseData());
                    String refreshToken = getRefreshTokenFromBuffer(responseDto.getResponseData());

                    TokenStorage.getTokenStorageInstance().write(accessToken, refreshToken);
                })
                .subscribe());
    }

    private String getAccessTokenFromBuffer(String buffer) {
        JSONObject object;
        String accessToken = "";
        try {
            object = new JSONObject(buffer);
            accessToken = object.getString(ACCESS_TOKEN_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    private String getRefreshTokenFromBuffer(String buffer) {
        JSONObject object;
        String refreshToken = "";
        try {
            object = new JSONObject(buffer);
            refreshToken = object.getString(REFRESH_TOKEN_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return refreshToken;
    }

    private RequestParameters getAccessTokenParameters(String authCode) {
        RequestMethods requestMethod = RequestMethods.POST;
        Map<String, Object> requestBodyParameters = new HashMap<>();
        requestBodyParameters.put(CODE_KEY, authCode);
        requestBodyParameters.put(CLIENT_ID_KEY, CLIENT_ID);
        requestBodyParameters.put(CLIENT_SECRET_KEY, CLIENT_SECRET);
        requestBodyParameters.put(GRANT_TYPE_KEY, AUTHORIZATION_CODE);
        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(CONTENT_TYPE_KEY, CONTENT_TYPE);

        RequestParameters requestParameters = new RequestParameters(BASE_URL, requestMethod, requestBodyParameters);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);
        return requestParameters;
    }

    public void refreshAccessToken(final AccessTokenUpdatedListener listener) {
        RequestParameters requestParameters = getRefreshTokenParameters();
        mCompositeDisposable.add(NetworkUtil.getResultFromServer(requestParameters)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(responseDto -> {
                    String accessToken = getAccessTokenFromBuffer(responseDto.getResponseData());
                    TokenStorage.getTokenStorageInstance().writeAccessToken(accessToken);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseDto>() {
                    @Override
                    public void onSuccess(ResponseDto responseDto) {
                        listener.onAccessTokenUpdated();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (RepositoryLoadHelper.isOnline()) {
                            listener.onFail();
                        }
                    }
                }));
    }

    private RequestParameters getRefreshTokenParameters() {
        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.POST;
        Map<String, Object> requestBodyParameters = new HashMap<>();
        requestBodyParameters.put(REFRESH_TOKEN_KEY, TokenStorage.getTokenStorageInstance().getRefreshToken());
        requestBodyParameters.put(CLIENT_ID_KEY, CLIENT_ID);
        requestBodyParameters.put(CLIENT_SECRET_KEY, CLIENT_SECRET);
        requestBodyParameters.put(GRANT_TYPE_KEY, REFRESH_TOKEN_KEY);
        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(CONTENT_TYPE_KEY, CONTENT_TYPE);

        RequestParameters requestParameters = new RequestParameters(BASE_URL, requestMethod, requestBodyParameters);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);
        return requestParameters;
    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getUserEmail() {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser != null) {
            return firebaseUser.getEmail();
        } else {
            return null;
        }
    }

    public Intent getGoogleSignInClientIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }

    public void closeAuthConnection() {
        mCompositeDisposable.clear();
    }

    public interface AccessTokenUpdatedListener {
        void onAccessTokenUpdated();

        void onFail();
    }
}
