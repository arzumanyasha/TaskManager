package com.example.arturarzumanyan.taskmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.ui.dialog.EventsDialog;
import com.example.arturarzumanyan.taskmanager.ui.fragment.EventsFragment;
import com.example.arturarzumanyan.taskmanager.ui.fragment.TasksFragment;
import com.squareup.picasso.Picasso;

import java.net.NoRouteToHostException;
import java.util.ArrayList;

public class IntentionActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "notification_channel";
    private final int NOTIFICATION_ID = 001;
    private NavigationView mNavigationView;
    private DrawerLayout drawer;
    private Intent mUserData;

    private ArrayList<TaskList> mTaskLists;

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
                if (getTitle() == "Events") {
                    Toast.makeText(getApplicationContext(), "Events", Toast.LENGTH_LONG).show();
                    openEventsDialog();
                }
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

        EventsRepository eventsRepository = new EventsRepository(this);
        eventsRepository.loadEvents(new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Event> eventsList) {

            }

            @Override
            public void onfail() {

            }
        });

        TaskListsRepository taskListsRepository = new TaskListsRepository(this);
        taskListsRepository.loadTaskLists(new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(ArrayList<TaskList> taskLists) {
                mTaskLists = taskLists;
                displayMenu();
                notifyDataLoaded();
            }

            @Override
            public void onfail() {

            }
        });

        /*
        TasksRepository tasksRepository = new TasksRepository(this);

        tasksRepository.loadTasks(mTaskLists.get(0), new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(ArrayList<Task> tasks) {
                displayMenu();
            }

            @Override
            public void onfail() {

            }
        });
*/
        drawer.closeDrawers();

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
        calendarMenu.add(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL))
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        EventsFragment eventsFragment = new EventsFragment();
                        openFragment(eventsFragment);
                        return false;
                    }
                });

        final SubMenu taskListsMenu = menu.addSubMenu("TaskLists");
        for (int i = 0; i < mTaskLists.size(); i++) {
            final int position = i + 1;
            taskListsMenu.add(mTaskLists.get(i).getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
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

    private void notifyDataLoaded(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_event_black_24dp);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.data_loaded_message));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    private void openEventsDialog(){
        EventsDialog eventsDialog = new EventsDialog();
        eventsDialog.show(getSupportFragmentManager(), "Events");
    }

    private void openFragment(Fragment fragment) {
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
}
