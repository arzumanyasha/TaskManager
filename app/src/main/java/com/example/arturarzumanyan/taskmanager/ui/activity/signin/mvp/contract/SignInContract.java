package com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.contract;

import android.content.Intent;

public class SignInContract {
    public interface SignInPresenter {
        void performSignIn();

        void setAuthenticationOptions();

        void processAuthWithGoogle(int requestCode, int resultCode, Intent data);

        void processCurrentUserSignInStatus();

        void unsubscribe();
    }

    public interface SignInView {
        void onSignInIntentReceived(Intent signInIntent, int authenticationRequestCode);

        void onSignInSuccess(String userName, String userEmail, String userPhotoUrl);

        void onSignInFailed(String message);
    }
}
