package com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp;

import android.content.Intent;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.google.firebase.auth.AuthResult;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;

public class SignInPresenterImpl implements SignInContract.SignInPresenter {
    private static final int AUTHENTICATION_REQUEST_CODE = 101;
    private static final String AUTHENTICATION_ERROR = "Authentication error";
    private SignInContract.SignInView mSignInView;

    public SignInPresenterImpl(SignInContract.SignInView signInView) {
        this.mSignInView = signInView;
    }

    @Override
    public void performSignIn() {
        Intent signInIntent = FirebaseWebService.getFirebaseWebServiceInstance().getGoogleSignInClientIntent();
        mSignInView.onSignInIntentReceived(signInIntent, AUTHENTICATION_REQUEST_CODE);
    }

    @Override
    public void setAuthenticationOptions() {
        FirebaseWebService.getFirebaseWebServiceInstance().setGoogleClientOptions();
    }

    @Override
    public void processAuthWithGoogle(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == AUTHENTICATION_REQUEST_CODE) {
            FirebaseWebService.getFirebaseWebServiceInstance().authWithGoogle(data).subscribe(new SingleObserver<AuthResult>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null && authResult.getUser() != null) {
                        Log.v("USER NAME " + authResult.getUser().getDisplayName());
                        mSignInView.onSignInSuccess(authResult.getUser().getDisplayName(),
                                authResult.getUser().getEmail(),
                                String.valueOf(authResult.getUser().getPhotoUrl()));
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mSignInView.onSignInFailed(AUTHENTICATION_ERROR);
                }
            });
        }
    }

    @Override
    public void processCurrentUserSignInStatus() {
        if (FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser() != null) {
            mSignInView.onSignInSuccess(FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser().getDisplayName(),
                    FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser().getEmail(),
                    String.valueOf(FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser().getPhotoUrl()));
        }
    }

    @Override
    public void unsubscribe() {
        FirebaseWebService.getFirebaseWebServiceInstance().closeAuthConnection();
        FirebaseWebService.getFirebaseWebServiceInstance().unsubscribe();
    }
}
