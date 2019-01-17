package com.example.arturarzumanyan.taskmanager.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.AllTaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.dialog.EventsDialog;
import com.example.arturarzumanyan.taskmanager.ui.dialog.TaskListsDialog;
import com.example.arturarzumanyan.taskmanager.ui.dialog.TasksDialog;
import com.example.arturarzumanyan.taskmanager.ui.fragment.EventsFragment;
import com.example.arturarzumanyan.taskmanager.ui.fragment.TasksFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IntentionActivity extends AppCompatActivity {
    public static final String EVENTS_KEY = "Events";
    public static final String TASKS_KEY = "Tasks";
    public static final String TASK_LISTS_KEY = "TaskLists";
    private final String CHANNEL_ID = "notification_channel";
    private final int NOTIFICATION_ID = 001;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private Intent mUserData;

    private SubMenu mTaskListsMenu;

    private TaskListsRepository mTaskListsRepository;
    private List<TaskList> mTaskLists;
    private TaskList mCurrentTaskList;

    private TaskListsRepository.OnTaskListsLoadedListener onTaskListsLoadedListener;
    private TaskFragmentInteractionListener taskFragmentInteractionListener;
    private EventFragmentInteractionListener eventFragmentInteractionListener;
    private TaskListFragmentInteractionListener taskListFragmentInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention);

        setViews();
        fetchUserInfoData();
        fetchTaskListsData();
    }

    private void setViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mDrawer.closeDrawers();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTitle() == EVENTS_KEY) {
                    openEventsDialog();
                } else {
                    openTasksDialog();
                }
            }
        });
    }

    private void fetchUserInfoData() {
        mUserData = getIntent();
    }

    private void fetchTaskListsData() {
        AllTaskListsSpecification allTaskListsSpecification = new AllTaskListsSpecification();

        onTaskListsLoadedListener = new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskLists) {
                displayDefaultTasksUi(taskLists);
            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {
                updateTaskListsMenu(taskLists);
            }

            @Override
            public void onSuccess(TaskList taskList) {

            }

            @Override
            public void onFail(String message) {
                Toast.makeText(IntentionActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };

        mTaskListsRepository = new TaskListsRepository();
        mTaskListsRepository.loadTaskLists(allTaskListsSpecification, onTaskListsLoadedListener);

    }

    private void displayDefaultTasksUi(List<TaskList> taskLists) {
        Log.v("Loaded tasklists");
        mTaskLists = taskLists;
        displayMenu();
        notifyDataLoaded();

        TasksFragment tasksFragment = TasksFragment.newInstance(mTaskLists.get(0));
        mCurrentTaskList = mTaskLists.get(0);
        openFragment(tasksFragment);
    }

    private void updateTaskListsMenu(List<TaskList> taskLists) {
        Log.v("TaskLists Menu updating");

        mTaskListsMenu.clear();

        mTaskLists = taskLists;
        for (int i = 0; i < mTaskLists.size(); i++) {
            final int position = i;
            mTaskListsMenu.add(mTaskLists.get(i).getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    updateTaskUi(mTaskLists.get(position));
                    return false;
                }
            });
        }
    }

    private void displayMenu() {
        Log.v("Displaying menu");

        setDisplayMenuViews();

        Menu mainMenu = mNavigationView.getMenu();
        mainMenu = populateCalendarMenu(mainMenu);
        populateTasksMenu(mainMenu);

        /*
        taskListsMenu.add("Add")
                .setIcon(R.drawable.ic_add_black_24dp)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });*/
    }

    private Menu populateCalendarMenu(Menu menu) {
        SubMenu calendarMenu = menu.addSubMenu("Calendars");
        calendarMenu.add(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL))
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.v(item.getTitle().toString() + " clicked");
                        EventsFragment eventsFragment = EventsFragment.newInstance();
                        openFragment(eventsFragment);
                        mDrawer.closeDrawer(Gravity.START);
                        invalidateOptionsMenu();
                        return false;
                    }
                });

        return menu;
    }

    private void populateTasksMenu(Menu menu) {
        mTaskListsMenu = menu.addSubMenu("TaskLists");
        for (int i = 0; i < mTaskLists.size(); i++) {
            final int position = i;
            mTaskListsMenu.add(mTaskLists.get(i).getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    updateTaskUi(mTaskLists.get(position));
                    invalidateOptionsMenu();
                    return false;
                }
            });
        }
    }

    private void setDisplayMenuViews() {
        ImageView userPhotoImageView = mNavigationView
                .getHeaderView(0)
                .findViewById(R.id.image_user_photo);
        TextView userNameTextView = mNavigationView
                .getHeaderView(0)
                .findViewById(R.id.text_user_name);
        TextView userEmailTextView = mNavigationView
                .getHeaderView(0)
                .findViewById(R.id.text_user_email);
        TextView addTaskListTextView = mNavigationView
                .findViewById(R.id.text_add_task_list);

        userNameTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_NAME));
        userEmailTextView.setText(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL));
        Picasso.get()
                .load(mUserData.getStringExtra(SignInActivity.EXTRA_USER_PHOTO_URL))
                .into(userPhotoImageView);

        addTaskListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(Gravity.START);
                openTaskListCreatingDialog();
            }
        });
    }

    private void notifyDataLoaded() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_event_black_24dp);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.data_loaded_message));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    private void openEventsDialog() {
        EventsDialog eventsDialog = EventsDialog.newInstance(null);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                eventFragmentInteractionListener.onEventsReady(events);
            }
        });
        eventsDialog.show(getSupportFragmentManager(), EVENTS_KEY);
    }

    private void openTasksDialog() {
        TasksDialog tasksDialog;
        for (TaskList taskList : mTaskLists) {
            if (mCurrentTaskList.getId() == taskList.getId()) {
                tasksDialog = TasksDialog.newInstance(null, taskList);
                tasksDialog.setTasksReadyListener(new TasksDialog.TasksReadyListener() {
                    @Override
                    public void onTasksReady(List<Task> tasks) {
                        taskFragmentInteractionListener.onTasksReady(tasks);
                    }
                });
                tasksDialog.show(getSupportFragmentManager(), TASKS_KEY);
                break;
            }
        }

    }

    private void openTaskListCreatingDialog() {
        TaskListsDialog taskListsDialog = TaskListsDialog.newInstance(null);
        taskListsDialog.setTaskListReadyListener(new TaskListsDialog.TaskListReadyListener() {
            @Override
            public void onTaskListReady(final TaskList taskList) {
                mTaskLists.add(taskList);
                mTaskListsMenu.add(taskList.getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        updateTaskUi(taskList);
                        return false;
                    }
                });
                mCurrentTaskList = taskList;
                openTaskFragment(taskList);
            }
        });
        taskListsDialog.show(getSupportFragmentManager(), TASK_LISTS_KEY);
    }

    private void updateTaskUi(TaskList taskList) {
        openTaskFragment(taskList);
        mCurrentTaskList = taskList;
        mDrawer.closeDrawer(Gravity.START);
    }

    private void openTaskFragment(TaskList taskList) {
        TasksFragment tasksFragment = TasksFragment.newInstance(taskList);
        openFragment(tasksFragment);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
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
        getMenuInflater().inflate(R.menu.date_picking, menu);
        getMenuInflater().inflate(R.menu.intention, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        updateActionBarMenuItems(menu);
        return true;
    }

    private void updateActionBarMenuItems(Menu menu) {
        if (!getTitle().equals(EVENTS_KEY)) {
            Log.v("TaskLists key");
            if (menu.findItem(R.id.pick_date) != null) {
                Log.v("intention");
                setActionBarMenuItemsVisibility(menu, false);
            }
        } else {
            Log.v("Events key");
            if (menu.findItem(R.id.update_task_list) != null &&
                    menu.findItem(R.id.delete_task_list) != null) {
                Log.v("datePicking");
                setActionBarMenuItemsVisibility(menu, true);
            }
        }
    }

    private void setActionBarMenuItemsVisibility(Menu menu, boolean visibility) {
        menu.findItem(R.id.pick_date).setVisible(visibility);
        menu.findItem(R.id.update_task_list).setVisible(!visibility);
        menu.findItem(R.id.delete_task_list).setVisible(!visibility);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.update_task_list: {
                if (!getTitle().equals(EVENTS_KEY)) {
                    displayTaskListUpdatingDialog(mCurrentTaskList);
                }
                break;
            }
            case R.id.delete_task_list: {
                if (!getTitle().equals(EVENTS_KEY)) {
                    deleteTaskList(mCurrentTaskList);
                }
                break;
            }
            case R.id.pick_date: {
                if (getTitle().equals(EVENTS_KEY)) {
                    displayEventDatePicker();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreviousFragment(TaskList taskList) {
        int menuSize = mTaskListsMenu.size();
        TaskList previousTaskList = null;
        for (int i = 0; i < menuSize; i++) {
            if (mTaskLists.get(i).getId() == taskList.getId()) {
                Log.v("menu size was " + mTaskListsMenu.size());
                mTaskListsMenu.getItem(i).setVisible(false);
                int itemId = mTaskListsMenu.getItem(i).getItemId();
                mTaskListsMenu.removeItem(itemId);
                Log.v("menu size is " + mTaskListsMenu.size());
                mTaskLists.remove(i);
                previousTaskList = mTaskLists.get(i - 1);
                break;
            }
        }
        if (previousTaskList != null) {
            mCurrentTaskList = previousTaskList;
            openTaskFragment(previousTaskList);
        }
    }

    private void updateTaskList(TaskList taskList) {
        for (int i = 0; i < mTaskListsMenu.size(); i++) {
            if (mTaskLists.get(i).getId() == taskList.getId()) {
                mTaskLists.get(i).setTitle(taskList.getTitle());
                mTaskListsMenu.getItem(i).setTitle(taskList.getTitle());
                taskListFragmentInteractionListener.onTaskListReady(taskList);
                break;
            }
        }
    }

    private void deleteTaskList(TaskList taskList) {
        mTaskListsRepository.deleteTaskList(taskList, new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskListArrayList) {
                openPreviousFragment(mCurrentTaskList);
            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {

            }

            @Override
            public void onSuccess(TaskList taskList) {

            }

            @Override
            public void onFail(String message) {
                Toast.makeText(IntentionActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayTaskListUpdatingDialog(TaskList taskList) {
        TaskListsDialog taskListsDialog = TaskListsDialog.newInstance(taskList);

        taskListsDialog.setTaskListReadyListener(new TaskListsDialog.TaskListReadyListener() {
            @Override
            public void onTaskListReady(TaskList taskList) {
                updateTaskList(taskList);
            }
        });
        taskListsDialog.show(getSupportFragmentManager(), TASK_LISTS_KEY);
    }

    private void displayEventDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                EventsRepository eventsRepository = new EventsRepository();

                EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
                eventsFromDateSpecification.setDate(DateUtils.getStringDateFromInt(year, monthOfYear, dayOfMonth));

                eventsRepository.getEvents(eventsFromDateSpecification, new EventsRepository.OnEventsLoadedListener() {
                    @Override
                    public void onSuccess(List<Event> eventsList) {

                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(IntentionActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });

            }
        }, DateUtils.getYear(), DateUtils.getMonth(), DateUtils.getDay());
        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onTaskListsLoadedListener = null;
        taskFragmentInteractionListener = null;
        eventFragmentInteractionListener = null;
        taskListFragmentInteractionListener = null;
    }

    public void setTaskFragmentInteractionListener(TaskFragmentInteractionListener listener) {
        this.taskFragmentInteractionListener = listener;
    }

    public void setEventFragmentInteractionListener(EventFragmentInteractionListener listener) {
        this.eventFragmentInteractionListener = listener;
    }

    public void setTaskListFragmentInteractionListener(TaskListFragmentInteractionListener listener) {
        this.taskListFragmentInteractionListener = listener;
    }

    public interface TaskFragmentInteractionListener {
        void onTasksReady(List<Task> tasks);
    }

    public interface EventFragmentInteractionListener {
        void onEventsReady(List<Event> events);
    }

    public interface TaskListFragmentInteractionListener {
        void onTaskListReady(TaskList taskList);
    }
}

