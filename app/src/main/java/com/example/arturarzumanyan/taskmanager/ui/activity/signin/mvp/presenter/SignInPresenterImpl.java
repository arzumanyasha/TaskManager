package com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.presenter;

import android.content.Intent;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.contract.SignInContract;

import static android.app.Activity.RESULT_OK;

public class SignInPresenterImpl implements SignInContract.SignInPresenter {
    private static final int AUTHENTICATION_REQUEST_CODE = 101;
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
        FirebaseWebService.getFirebaseWebServiceInstance().setUserInfoLoadingListener(new FirebaseWebService.UserInfoLoadingListener() {
            @Override
            public void onDataLoaded(String userName, String userEmail, String userPhotoUrl) {
                mSignInView.onSignInSuccess(userName, userEmail, userPhotoUrl);
            }

            @Override
            public void onFail(String message) {
                mSignInView.onSignInFailed(message);
            }
        });
    }

    @Override
    public void processAuthWithGoogle(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == AUTHENTICATION_REQUEST_CODE) {
            FirebaseWebService.getFirebaseWebServiceInstance().authWithGoogle(data);
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
