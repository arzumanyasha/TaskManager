package com.example.arturarzumanyan.taskmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.google.android.gms.common.SignInButton;

public class SignInActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    public static final String EXTRA_USER_NAME = "userName";
    public static final String EXTRA_USER_EMAIL = "userEmail";
    public static final String EXTRA_USER_PHOTO_URL = "userPhotoUrl";

    private FirebaseWebService mFirebaseWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseWebService = new FirebaseWebService();
        mFirebaseWebService.setGoogleClient(this);
        mFirebaseWebService.setUserInfoLoadingListener(new FirebaseWebService.UserInfoLoadingListener() {
            @Override
            public void onDataLoaded(String userName, String userEmail, String userPhotoUrl) {
                updateUI(userName, userEmail, userPhotoUrl);
            }
        });

        SignInButton signInButton = findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mFirebaseWebService.getCurrentUser() != null) {
            updateUI(mFirebaseWebService.getCurrentUser().getDisplayName(),
                    mFirebaseWebService.getCurrentUser().getEmail(),
                    mFirebaseWebService.getCurrentUser().getPhotoUrl().toString());

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
        Intent signInIntent = mFirebaseWebService.getGoogleSignInClientIntent();

        startActivityForResult(signInIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {

            mFirebaseWebService.authWithGoogle(data);
        }
    }

    @Override
    protected void onDestroy() {
        mFirebaseWebService.closeAuthConnection();
        super.onDestroy();
    }
}
