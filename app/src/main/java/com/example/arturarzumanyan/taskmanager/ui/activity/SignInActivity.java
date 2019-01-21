package com.example.arturarzumanyan.taskmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.google.android.gms.common.SignInButton;

public class SignInActivity extends BaseActivity {

    private static final int AUTHENTICATION_REQUEST_CODE = 101;
    public static final String EXTRA_USER_NAME = "userName";
    public static final String EXTRA_USER_EMAIL = "userEmail";
    public static final String EXTRA_USER_PHOTO_URL = "userPhotoUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setViews();
        setAuthenticationOptions();
    }

    private void setViews() {
        SignInButton signInButton = findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void setAuthenticationOptions() {
        FirebaseWebService.getFirebaseWebServiceInstance().setGoogleClientOptions();
        FirebaseWebService.getFirebaseWebServiceInstance().setUserInfoLoadingListener(new FirebaseWebService.UserInfoLoadingListener() {
            @Override
            public void onDataLoaded(String userName, String userEmail, String userPhotoUrl) {
                updateUI(userName, userEmail, userPhotoUrl);
            }

            @Override
            public void onFail(String message) {
                onError(message);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser() != null) {
            updateUI(FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser().getDisplayName(),
                    FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser().getEmail(),
                    String.valueOf(FirebaseWebService.getFirebaseWebServiceInstance().getCurrentUser().getPhotoUrl()));

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.failed_log_in_message), Toast.LENGTH_LONG).show();
        }
    }

    public void updateUI(String userName, String userEmail, String userPhotoUrl) {
        Toast.makeText(getApplicationContext(), getString(R.string.logged_in_message), Toast.LENGTH_LONG).show();

        Intent accountIntent = new Intent(SignInActivity.this, IntentionActivity.class);
        accountIntent.putExtra(EXTRA_USER_NAME, userName);
        accountIntent.putExtra(EXTRA_USER_EMAIL, userEmail);
        accountIntent.putExtra(EXTRA_USER_PHOTO_URL, userPhotoUrl);
        startActivity(accountIntent);
        finish();
    }

    private void signIn() {
        Intent signInIntent = FirebaseWebService.getFirebaseWebServiceInstance().getGoogleSignInClientIntent();

        startActivityForResult(signInIntent, AUTHENTICATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AUTHENTICATION_REQUEST_CODE) {
                FirebaseWebService.getFirebaseWebServiceInstance().authWithGoogle(data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        FirebaseWebService.getFirebaseWebServiceInstance().closeAuthConnection();
        super.onDestroy();
    }
}
