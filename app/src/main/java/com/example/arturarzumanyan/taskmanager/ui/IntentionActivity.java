package com.example.arturarzumanyan.taskmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.SubMenu;
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
    private EventsCloudRepository eventsCloudRepository;
    private TaskListsCloudRepository taskListsCloudRepository;

    private NavigationView mNavigationView;
    private DrawerLayout drawer;
    private Intent mUserData;

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

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

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mUserData = getIntent();

        mNavigationView = findViewById(R.id.nav_view);

        eventsDbRepository = new EventsDbRepository();
        taskListsDbRepository = new TaskListsDbRepository();
        tasksCloudRepository = new TasksCloudRepository();
        tasksDbRepository = new TasksDbRepository();
        eventsCloudRepository = new EventsCloudRepository();

        try {
            if ((tasksDbRepository.getTasksFromTaskList(this, 1).size() == 0) &&
                    (taskListsDbRepository.getTaskLists(this).size() == 0) &&
                    (eventsDbRepository.getEvents(this).size() == 0)) {
                loadUserData();
            } else
                displayMenu();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Task> tasks = tasksDbRepository.getTasksFromTaskList(this, 3);
            ArrayList<TaskList> taskListArrayList = taskListsDbRepository.getTaskLists(this);
            ArrayList<Event> events1 = eventsDbRepository.getEvents(this);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        drawer.closeDrawers();

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("taskListId", "1");
        TasksFragment tasksFragment = new TasksFragment();
        tasksFragment.setArguments(bundle);
        openFragment(tasksFragment);
    }

    private void displayMenu() {
        ImageView userPhotoImageView = mNavigationView
                .getHeaderView(0)
                .findViewById(R.id.imageViewUserPhoto);
        TextView userNameTextView = mNavigationView
                .getHeaderView(0)
                .findViewById(R.id.textViewUserName);
        TextView userEmailTextView = mNavigationView
                .getHeaderView(0)
                .findViewById(R.id.textViewUserEmail);
        userNameTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_NAME));
        userEmailTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL));
        Picasso.get()
                .load(mUserData.getStringExtra(SignInActivity.EXTRA_USER_PHOTO_URL))
                .into(userPhotoImageView);

        Menu menu = mNavigationView.getMenu();
        SubMenu calendarMenu = menu.addSubMenu("Calendars");
        calendarMenu.add(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL));

        SubMenu taskListsMenu = menu.addSubMenu("TaskLists");

        ArrayList<TaskList> taskListArrayList = taskListsDbRepository.getTaskLists(IntentionActivity.this);
        for (int i = 0; i < taskListArrayList.size(); i++) {
            final int position = i + 1;
            taskListsMenu.add(taskListArrayList.get(i).getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    fragmentManager = getSupportFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("taskListId", Integer.toString(position));
                    TasksFragment tasksFragment = new TasksFragment();
                    tasksFragment.setArguments(bundle);
                    openFragment(tasksFragment);
                    return false;
                }
            });
        }
    }

    private void loadUserData() {
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

        taskListsCloudRepository = new TaskListsCloudRepository();
        taskListsCloudRepository.getTaskLists(this, new TaskListsCloudRepository.OnTaskCompletedListener() {
            @Override
            public void onSuccess(ArrayList<TaskList> taskListArrayList) {
                taskListsDbRepository.addTaskLists(IntentionActivity.this, taskListArrayList);
                ArrayList<TaskList> taskLists = taskListsDbRepository.getTaskLists(IntentionActivity.this);
                for (int i = 0; i < taskLists.size(); i++) {
                    final int position = i;
                    tasksCloudRepository.getTasksFromTaskList(IntentionActivity.this,
                            taskLists.get(i),
                            new TasksCloudRepository.OnTaskCompletedListener() {
                                @Override
                                public void onSuccess(ArrayList<Task> taskArrayList) {
                                    tasksDbRepository.addTasks(IntentionActivity.this, taskArrayList);
                                    if (position == taskListsDbRepository.getTaskLists(IntentionActivity.this).size() - 1) {
                                        displayMenu();
                                    }
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
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
/*
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
