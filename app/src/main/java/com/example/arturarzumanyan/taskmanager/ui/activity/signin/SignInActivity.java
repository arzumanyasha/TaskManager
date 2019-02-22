package com.example.arturarzumanyan.taskmanager.ui.activity.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.contract.SignInContract;
import com.example.arturarzumanyan.taskmanager.ui.activity.signin.mvp.presenter.SignInPresenter;
import com.google.android.gms.common.SignInButton;

public class SignInActivity extends BaseActivity implements SignInContract.SignInView {

    public static final String EXTRA_USER_NAME = "userName";
    public static final String EXTRA_USER_EMAIL = "userEmail";
    public static final String EXTRA_USER_PHOTO_URL = "userPhotoUrl";

    private SignInPresenter mSignInPresenter;


    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setViews();
        mSignInPresenter = new SignInPresenter(this);
        mSignInPresenter.setAuthenticationOptions();
    }

    private void setViews() {
        signInButton = findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInPresenter.performSignIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mSignInPresenter.processCurrentUserSignInStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("START ACTIVITY FOR RESULT RESULT CODE = " + resultCode);
        mSignInPresenter.processAuthWithGoogle(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        unsubscribe();
        super.onDestroy();
    }

    private void unsubscribe() {
        mSignInPresenter.unsubscribe();
        mSignInPresenter = null;
        signInButton.setOnClickListener(null);
    }

    @Override
    public void onSignInIntentReceived(Intent signInIntent, int authenticationRequestCode) {
        startActivityForResult(signInIntent, authenticationRequestCode);
    }

    @Override
    public void onSignInSuccess(String userName, String userEmail, String userPhotoUrl) {
        Toast.makeText(getApplicationContext(), getString(R.string.logged_in_message), Toast.LENGTH_LONG).show();

        Intent accountIntent = new Intent(SignInActivity.this, IntentionActivity.class);
        accountIntent.putExtra(EXTRA_USER_NAME, userName);
        accountIntent.putExtra(EXTRA_USER_EMAIL, userEmail);
        accountIntent.putExtra(EXTRA_USER_PHOTO_URL, userPhotoUrl);
        startActivity(accountIntent);
        finish();
    }

    @Override
    public void onSignInFailed(String message) {
        onError(message);
    }
}
