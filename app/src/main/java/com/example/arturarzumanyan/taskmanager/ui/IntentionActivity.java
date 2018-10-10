package com.example.arturarzumanyan.taskmanager.ui;

import android.content.Intent;
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
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsCloudRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsDbRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsCloudRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsDbRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksDbRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

public class IntentionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EventsDbRepository eventsDbRepository;
    private TaskListsDbRepository taskListsDbRepository;
    private TasksDbRepository tasksDbRepository;
    private TasksCloudRepository tasksCloudRepository;

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

        eventsDbRepository = new EventsDbRepository();
        EventsCloudRepository eventsCloudRepository = new EventsCloudRepository();
        eventsCloudRepository.getEvents(this, new EventsCloudRepository.OnTaskCompletedListener() {
            @Override
            public void onSuccess(ArrayList<Event> eventsList) {
                ArrayList<Event> events = eventsList;
                eventsDbRepository.addEvents(IntentionActivity.this, events);
            }

            @Override
            public void onfail() {

            }
        });

        taskListsDbRepository = new TaskListsDbRepository();

        tasksCloudRepository = new TasksCloudRepository();
        tasksDbRepository = new TasksDbRepository();

        TaskListsCloudRepository taskListsCloudRepository = new TaskListsCloudRepository();
        taskListsCloudRepository.getTaskLists(this, new TaskListsCloudRepository.OnTaskCompletedListener() {
            @Override
            public void onSuccess(ArrayList<TaskList> taskListArrayList) {
                taskListsDbRepository.addTaskLists(IntentionActivity.this, taskListArrayList);
                ArrayList<TaskList> taskLists = taskListsDbRepository.getTaskLists(IntentionActivity.this);
                for (int i = 0; i < taskLists.size(); i++) {
                    tasksCloudRepository.getTasksFromTaskList(IntentionActivity.this,
                            taskLists.get(i),
                            new TasksCloudRepository.OnTaskCompletedListener() {
                                @Override
                                public void onSuccess(ArrayList<Task> taskArrayList) {
                                    tasksDbRepository.addTasks(IntentionActivity.this, taskArrayList);
                                }

                                @Override
                                public void onfail() {

                                }
                            });
                }
            }

            @Override
            public void onfail() {

            }
        });

        try {
            ArrayList<Task> tasks = tasksDbRepository.getTasksFromTaskList(this, 3);
            ArrayList<TaskList> taskListArrayList = taskListsDbRepository.getTaskLists(this);
            ArrayList<Event> events1 = eventsDbRepository.getEvents(this);

        } catch (ParseException e) {
            e.printStackTrace();
        }
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
