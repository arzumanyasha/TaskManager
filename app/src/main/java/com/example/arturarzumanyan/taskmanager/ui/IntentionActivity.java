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
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class IntentionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String BASE_EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/";
    private static final String BASE_TASK_LISTS_URL = "https://www.googleapis.com/tasks/v1/users/@me/lists";
    private static final String BASE_TASKS_URL = "https://www.googleapis.com/tasks/v1/lists/";
    private static final String AUTHORIZATION_KEY = "Authorization";

    private SQLiteDbHelper sqliteDbHelper;

    private UserDataAsyncTask mUserRefreshEventsAsyncTask;
    private ArrayList<UserDataAsyncTask> mUserTasksAsyncTaskList = new ArrayList<>();

    private String mEventsUrl;

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

        Intent mUserData = getIntent();
        ImageView userPhotoImageView = navigationView
                .getHeaderView(0)
                .findViewById(R.id.imageViewUserPhoto);
        TextView userNameTextView = navigationView
                .getHeaderView(0)
                .findViewById(R.id.textViewUserName);
        TextView userEmailTextView = navigationView
                .getHeaderView(0)
                .findViewById(R.id.textViewUserEmail);
        userNameTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_NAME));
        userEmailTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL));
        Picasso.get()
                .load(mUserData.getStringExtra(SignInActivity.EXTRA_USER_PHOTO_URL))
                .into(userPhotoImageView);

        sqliteDbHelper = new SQLiteDbHelper(this);

        UserDataAsyncTask mUserEventsAsyncTask = new UserDataAsyncTask();
        UserDataAsyncTask mUserTaskListsAsyncTask = new UserDataAsyncTask();
        mUserRefreshEventsAsyncTask = new UserDataAsyncTask();

        mEventsUrl = BASE_EVENTS_URL + mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL) + "/events";
        requestUserData(mUserEventsAsyncTask, mEventsUrl);

        requestUserData(mUserTaskListsAsyncTask, BASE_TASK_LISTS_URL);

        mUserRefreshEventsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                EventsParser eventsParser = new EventsParser();
                sqliteDbHelper.insertEvents(eventsParser.parseEvents(response));
            }
        });

        mUserEventsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                if (response.equals("")) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(IntentionActivity.this);
                    requestUserData(mUserRefreshEventsAsyncTask, mEventsUrl);
                } else {
                    EventsParser eventsParser = new EventsParser();
                    sqliteDbHelper.insertEvents(eventsParser.parseEvents(response));
                }
            }
        });

        mUserTaskListsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException {
                if (response.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    storeTaskLists(response);
                    loadTasks();
                }
            }
        });


        try {
            ArrayList<Event> eventsList = sqliteDbHelper.getEvents();
            ArrayList<TaskList> taskListArrayList = sqliteDbHelper.getTaskLists();
            ArrayList<Task> tasksArrayList = sqliteDbHelper.getTasksFromList(1);
            int size = eventsList.size();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        ArrayList<TaskList> taskListArrayList = sqliteDbHelper.getTaskLists();
        for (int i = 0; i < taskListArrayList.size(); i++) {
            final int taskListId = taskListArrayList.get(i).getId();
            String mTasksUrl = BASE_TASKS_URL + taskListArrayList.get(i).getTaskListId() + "/tasks?showHidden=true";
            mUserTasksAsyncTaskList.add(new UserDataAsyncTask());
            requestUserData(mUserTasksAsyncTaskList.get(i), mTasksUrl);
            mUserTasksAsyncTaskList.get(i).setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
                @Override
                public void onDataLoaded(String response) throws JSONException, ParseException {
                    TasksParser tasksParser = new TasksParser();
                    sqliteDbHelper.insertTasks(tasksParser.parseTasks(response, taskListId));
                }
            });
        }
    }

    private void storeTaskLists(String response) throws JSONException {
        TaskListsParser taskListsParser = new TaskListsParser();
        ArrayList<TaskList> taskListArrayList = taskListsParser.parseTaskLists(response);
        for (int i = 0; i < taskListArrayList.size(); i++) {
            mUserTasksAsyncTaskList.add(new UserDataAsyncTask());
        }
        sqliteDbHelper.insertTaskLists(taskListArrayList);
    }

    private void requestUserData(UserDataAsyncTask asyncTask, String url) {
        TokenStorage tokenStorage = new TokenStorage();

        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        HashMap<String, String> requestBodyParameters = new HashMap<>();
        HashMap<String, String> requestHeaderParameters = new HashMap<>();
        String token = tokenStorage.getAccessToken(this);
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + tokenStorage.getAccessToken(this));
        RequestParameters requestParameters = new RequestParameters(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
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
