package com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.contract;

import android.content.Intent;

public class SignInContract {
    public interface SignInPresenter {
        void performSignIn();

        void setAuthenticationOptions();

        void authWithGoogle(Intent data);

        boolean checkIsAuthenticationResultOk(int requestCode, int resultCode);

        void checkIsCurrentUserNull();

        void unsubscribe();
    }

    public interface SignInView {
        void onSignInIntentReceived(Intent signInIntent, int authenticationRequestCode);

        void onSignInSuccess(String userName, String userEmail, String userPhotoUrl);

        void onSignInFailed(String message);
    }
}
