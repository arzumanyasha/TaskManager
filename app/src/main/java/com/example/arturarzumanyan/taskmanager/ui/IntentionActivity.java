package com.example.arturarzumanyan.taskmanager.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.db.EventsDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class IntentionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String BASE_EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/";
    private static final String AUTHORIZATION_KEY = "Authorization";

    private TextView userNameTextView, userEmailTextView;
    private ImageView userPhotoImageView;
    private Intent mUserData;
    private EventsDbHelper eventsDbHelper;

    private UserDataAsyncTask mUserEventsAsyncTask;
    private UserDataAsyncTask mUserRefreshEventsAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mUserData = getIntent();
        userPhotoImageView = navigationView
                .getHeaderView(0)
                .findViewById(R.id.imageViewUserPhoto);
        userNameTextView = navigationView
                .getHeaderView(0)
                .findViewById(R.id.textViewUserName);
        userEmailTextView = navigationView
                .getHeaderView(0)
                .findViewById(R.id.textViewUserEmail);
        userNameTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_NAME));
        userEmailTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL));
        Picasso.get()
                .load(mUserData.getStringExtra(SignInActivity.EXTRA_USER_PHOTO_URL))
                .into(userPhotoImageView);

        eventsDbHelper = new EventsDbHelper(this);

        mUserEventsAsyncTask = new UserDataAsyncTask();
        mUserRefreshEventsAsyncTask = new UserDataAsyncTask();

        getUserEvents(mUserEventsAsyncTask, mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL));

        mUserRefreshEventsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                EventsParser eventsParser = new EventsParser();
                eventsDbHelper.insertEvents(eventsParser.parseEvents(response));
            }
        });

        mUserEventsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                if (response.equals("")) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(IntentionActivity.this);
                    getUserEvents(mUserRefreshEventsAsyncTask, mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL));
                } else {
                    EventsParser eventsParser = new EventsParser();
                    eventsDbHelper.insertEvents(eventsParser.parseEvents(response));
                }
            }
        });

        try {
            ArrayList<Event> eventsList = eventsDbHelper.getEvents();
            int size = eventsList.size();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getUserEvents(UserDataAsyncTask asyncTask, String userEmail) {
        TokenStorage tokenStorage = new TokenStorage();

        String url = BASE_EVENTS_URL + userEmail + "/events";
        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        HashMap<String, String> requestBodyParameters = new HashMap<>();
        HashMap<String, String> requestHeaderParameters = new HashMap<>();
        String token = tokenStorage.getAccessToken(this);
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + tokenStorage.getAccessToken(this));
        RequestParameters requestParameters = new RequestParameters(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
        asyncTask.execute(requestParameters);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.intention, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
