package com.example.arturarzumanyan.taskmanager.ui.adapter.task;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.contract.TasksContract;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksViewHolder> {
    private TasksContract.TasksPresenter mTasksListPresenter;

    public TasksAdapter(TasksContract.TasksPresenter mTasksListPresenter) {
        this.mTasksListPresenter = mTasksListPresenter;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TasksViewHolder(view, mTasksListPresenter);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksViewHolder holder, int position) {
        mTasksListPresenter.onBindEventsRowViewAtPosition(position, holder);
    }

    @Override
    public int getItemCount() {
        return mTasksListPresenter.getTasksRowsCount();
    }

    public void updateList(List<Task> updatedList) {
        mTasksListPresenter.updateTasksList(updatedList);
        notifyDataSetChanged();
    }

    public void unsubscribe() {
        mTasksListPresenter.unsubscribe();
    }
}
