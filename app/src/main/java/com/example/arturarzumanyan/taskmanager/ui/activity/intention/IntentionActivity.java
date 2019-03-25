package com.example.arturarzumanyan.taskmanager.ui.activity.intention;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp.IntentionContract;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp.IntentionPresenterImpl;
import com.example.arturarzumanyan.taskmanager.ui.activity.signin.SignInActivity;
import com.example.arturarzumanyan.taskmanager.ui.dialog.event.EventsDialog;
import com.example.arturarzumanyan.taskmanager.ui.dialog.task.TasksDialog;
import com.example.arturarzumanyan.taskmanager.ui.dialog.tasklist.TaskListsDialog;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.container.EventsFragment;
import com.example.arturarzumanyan.taskmanager.ui.fragment.task.TasksFragment;
import com.example.arturarzumanyan.taskmanager.ui.util.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IntentionActivity extends BaseActivity implements IntentionContract.IntentionView {
    public static final String EVENTS_KEY = "Events";
    public static final String TASKS_KEY = "Tasks";
    public static final String TASK_LISTS_KEY = "TaskLists";
    private static final String TITLE_KEY = "title";
    private static final String RETAINED_TASK_FRAGMENT_TAG = "RetainedTaskFragment";
    private static final String RETAINED_EVENT_FRAGMENT_TAG = "RetainedEventFragment";
    private final String CHANNEL_ID = "notification_channel";
    private final int NOTIFICATION_ID = 001;
    private NavigationView mNavigationView;
    private FloatingActionButton mAddButton;
    private DrawerLayout mDrawer;
    private Intent mUserData;

    private IntentionContract.IntentionPresenter mIntentionPresenter;
    private SubMenu mTaskListsMenu;
    private Menu mActionBarMenu;

    private TaskFragmentInteractionListener taskFragmentInteractionListener;
    private EventFragmentInteractionListener eventFragmentInteractionListener;
    private TaskListFragmentInteractionListener taskListFragmentInteractionListener;

    private TasksFragment mRetainedTasksFragment;
    private EventsFragment mRetainedEventsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intention);

        getUserInfoData();
        setViews();

        if (savedInstanceState == null) {
            mIntentionPresenter = new IntentionPresenterImpl(this);
            mIntentionPresenter.fetchTaskListsData();
        } else {
            mIntentionPresenter = (IntentionContract.IntentionPresenter) getLastCustomNonConfigurationInstance();
            mIntentionPresenter.attachView(this);
            mIntentionPresenter.processRestoredInfo(savedInstanceState.getString(TITLE_KEY, TASK_LISTS_KEY));
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mIntentionPresenter;
    }

    private void setViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mAddButton = findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mDrawer.closeDrawers();

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntentionPresenter.processAddButtonClick(getTitle().toString());
            }
        });
    }

    private TasksFragment getRetainedTaskFragment() {
        mRetainedTasksFragment = (TasksFragment) getSupportFragmentManager().findFragmentByTag(RETAINED_TASK_FRAGMENT_TAG);
        return mRetainedTasksFragment;
    }

    private EventsFragment getRetainedEventsFragment() {
        mRetainedEventsFragment = (EventsFragment) getSupportFragmentManager().findFragmentByTag(RETAINED_EVENT_FRAGMENT_TAG);
        return mRetainedEventsFragment;
    }

    private void getUserInfoData() {
        mUserData = getIntent();
    }

    @Override
    public void displayDefaultUi(List<TaskList> taskLists) {
        displayMenu(taskLists);
        notifyDataLoaded();
    }

    @Override
    public void displayDefaultTasksUi(TaskList taskList) {
        Log.v("Loaded tasklists");

        mRetainedTasksFragment = getRetainedTaskFragment();
        if (mRetainedTasksFragment == null) {
            mRetainedTasksFragment = TasksFragment.newInstance(taskList);
            mRetainedEventsFragment = null;
        }
        openRetainedFragment(mRetainedTasksFragment, RETAINED_TASK_FRAGMENT_TAG);
    }

    @Override
    public void displayRestoredEventsUi() {
        mRetainedEventsFragment = getRetainedEventsFragment();
        if (mRetainedEventsFragment == null) {
            mRetainedEventsFragment = EventsFragment.newInstance();
            mRetainedTasksFragment = null;
        }
        openRetainedFragment(mRetainedEventsFragment, RETAINED_EVENT_FRAGMENT_TAG);
    }

    @Override
    public void recreateTaskListsMenu(List<TaskList> taskLists) {
        Log.v("TaskLists Menu updating");

        mTaskListsMenu.clear();

        for (final TaskList taskList : taskLists) {
            mTaskListsMenu.add(taskList.getTitle()).setOnMenuItemClickListener(item -> {
                mIntentionPresenter.processTaskListMenuItemClick(taskList);
                return false;
            });
        }
    }

    private void displayMenu(List<TaskList> taskLists) {
        Log.v("Displaying menu");

        setDisplayMenuViews();

        Menu mainMenu = mNavigationView.getMenu();
        mainMenu = populateCalendarMenu(mainMenu);
        populateTasksMenu(taskLists, mainMenu);
        displaySignOutMenu(mainMenu);
    }

    private void displaySignOutMenu(Menu menu) {
        SubMenu signOutMenu = menu.addSubMenu("Sign Out");
        signOutMenu.add("Log Out").setOnMenuItemClickListener(item -> {
            mIntentionPresenter.processLogOut();
            return false;
        });
    }

    @Override
    public void displaySignInScreen() {
        Intent intent = new Intent(IntentionActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    private Menu populateCalendarMenu(Menu menu) {
        SubMenu calendarMenu = menu.addSubMenu("Calendars");
        calendarMenu.add(mUserData.getStringExtra(SignInActivity.EXTRA_USER_EMAIL))
                .setOnMenuItemClickListener(item -> {
                    openCalendarFragment();
                    return false;
                });

        return menu;
    }

    private void openCalendarFragment() {
        mRetainedEventsFragment = getRetainedEventsFragment();
        if (mRetainedEventsFragment == null) {
            mRetainedEventsFragment = EventsFragment.newInstance();
        }
        mRetainedTasksFragment = null;
        openRetainedFragment(mRetainedEventsFragment, RETAINED_EVENT_FRAGMENT_TAG);
        mDrawer.closeDrawer(Gravity.START);
        invalidateOptionsMenu();
    }

    private void populateTasksMenu(List<TaskList> taskLists, Menu menu) {
        mTaskListsMenu = menu.addSubMenu("TaskLists");
        for (final TaskList taskList : taskLists) {
            mTaskListsMenu.add(taskList.getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.v("Populating tasks menu " + taskList.getTitle());
                    mIntentionPresenter.processTaskListMenuItemClick(taskList);
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
                .transform(new CircleTransformation())
                .into(userPhotoImageView);

        addTaskListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(Gravity.START);
                mIntentionPresenter.processTaskListCreatingDialog();
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

    @Override
    public void updateTaskUi(TaskList taskList) {
        mRetainedEventsFragment = null;
        updateRetainedTasksFragment(taskList);
        mDrawer.closeDrawer(Gravity.START);
        invalidateOptionsMenu();
    }

    private void openRetainedFragment(Fragment retainedFragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, retainedFragment, tag)
                .commit();
    }

    @Override
    public void onFail(String message) {
        onError(message);
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
        mActionBarMenu = menu;

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        mActionBarMenu = menu;
        mIntentionPresenter.processActionBarMenuItems(getTitle().toString());
        return true;
    }

    public void setFloatingActionButtonInvisible() {
        mAddButton.setVisibility(View.INVISIBLE);
    }

    public void setFloatingActionButtonVisible() {
        mAddButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setActionBarMenuItemsVisibility(boolean visibility) {
        mActionBarMenu.findItem(R.id.update_task_list).setVisible(visibility);
        mActionBarMenu.findItem(R.id.delete_task_list).setVisible(visibility);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.update_task_list: {
                mIntentionPresenter.processTaskListUpdatingDialog(getTitle().toString());
                break;
            }
            case R.id.delete_task_list: {
                mIntentionPresenter.deleteTaskList(getTitle().toString());
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayPreviousTaskFragment(List<TaskList> taskLists, TaskList taskList) {
        recreateTaskListsMenu(taskLists);
        updateRetainedTasksFragment(taskList);
    }

    private void updateRetainedTasksFragment(TaskList taskList) {
        mRetainedTasksFragment = getRetainedTaskFragment();
        mRetainedEventsFragment = null;
        if (mRetainedTasksFragment == null) {
            Log.v("Retained fragment is null");
            mRetainedTasksFragment = TasksFragment.newInstance(taskList);
            openRetainedFragment(mRetainedTasksFragment, RETAINED_TASK_FRAGMENT_TAG);
        } else {
            Log.v("Retained fragment is not null");
            mRetainedTasksFragment.setTaskList(taskList);
        }
    }

    @Override
    public void updateTaskListOnUi(TaskList taskList, int index) {
        mTaskListsMenu.getItem(index).setTitle(taskList.getTitle());
        taskListFragmentInteractionListener.onTaskListReady(taskList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TITLE_KEY, getTitle().toString());
    }

    @Override
    public void showEventCreatingDialog() {
        EventsDialog eventsDialog = EventsDialog.newInstance(null);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mIntentionPresenter.processUpdatedEventsList(events);
            }
        });
        eventsDialog.show(getSupportFragmentManager(), EVENTS_KEY);
    }

    @Override
    public void showTaskCreatingDialog(TaskList taskList) {
        TasksDialog tasksDialog = TasksDialog.newInstance(null, taskList);
        tasksDialog.setTasksReadyListener(new TasksDialog.TasksReadyListener() {
            @Override
            public void onTasksReady(List<Task> tasks) {
                mIntentionPresenter.processUpdatedTasksList(tasks);
            }
        });
        tasksDialog.show(getSupportFragmentManager(), TASKS_KEY);
    }

    @Override
    public void showTaskListCreatingDialog() {
        TaskListsDialog taskListsDialog = TaskListsDialog.newInstance(null);
        taskListsDialog.setTaskListReadyListener(new TaskListsDialog.TaskListReadyListener() {
            @Override
            public void onTaskListReady(final TaskList taskList) {
                mIntentionPresenter.processCreatedTaskList(taskList);
            }
        });
        taskListsDialog.show(getSupportFragmentManager(), TASK_LISTS_KEY);
    }

    @Override
    public void showTaskListUpdatingDialog(TaskList taskList) {
        TaskListsDialog taskListsDialog = TaskListsDialog.newInstance(taskList);
        taskListsDialog.setTaskListReadyListener(new TaskListsDialog.TaskListReadyListener() {
            @Override
            public void onTaskListReady(TaskList taskList) {
                mIntentionPresenter.processUpdatedTaskList(taskList);
            }
        });
        taskListsDialog.show(getSupportFragmentManager(), TASK_LISTS_KEY);
    }

    @Override
    public void onTasksReady(List<Task> tasks) {
        taskFragmentInteractionListener.onTasksReady(tasks);
    }

    @Override
    public void onEventsReady(List<Event> events) {
        eventFragmentInteractionListener.onEventsReady(events);
    }

    @Override
    public void onTaskListReady(final TaskList taskList) {
        mTaskListsMenu.add(taskList.getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mIntentionPresenter.processTaskListMenuItemClick(taskList);
                return false;
            }
        });
        updateRetainedTasksFragment(taskList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIntentionPresenter.unsubscribe();
        unsubscribeEventListeners();
        unsubscribeTaskListeners();
    }

    public void unsubscribeTaskListeners() {
        taskListFragmentInteractionListener = null;
        taskFragmentInteractionListener = null;
    }

    public void unsubscribeEventListeners() {
        eventFragmentInteractionListener = null;
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

