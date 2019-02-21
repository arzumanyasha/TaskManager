package com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.presenter;

import android.content.Intent;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.contract.SignInContract;

import static android.app.Activity.RESULT_OK;

public class SignInPresenter implements SignInContract.SignInPresenter {
    private static final int AUTHENTICATION_REQUEST_CODE = 101;
    private SignInContract.SignInView mSignInView;

    public SignInPresenter(SignInContract.SignInView mSignInView) {
        this.mSignInView = mSignInView;
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
    public void authWithGoogle(Intent data) {
        FirebaseWebService.getFirebaseWebServiceInstance().authWithGoogle(data);
    }

    @Override
    public boolean checkIsAuthenticationResultOk(int requestCode, int resultCode) {
        return resultCode == RESULT_OK && requestCode == AUTHENTICATION_REQUEST_CODE;
    }

    @Override
    public void checkIsCurrentUserNull() {
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
