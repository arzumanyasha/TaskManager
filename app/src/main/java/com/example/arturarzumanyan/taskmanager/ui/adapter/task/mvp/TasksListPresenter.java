package com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp;

import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.List;

public class TasksListPresenter {
    private List<Task> mTasksList;
    private OnItemClickListener mListener;

    public TasksListPresenter(List<Task> mTasksList, OnItemClickListener mListener) {
        this.mTasksList = mTasksList;
        this.mListener = mListener;
    }

    public void onBindEventsRowViewAtPosition(int position, TaskRowView rowView) {
        Task task = mTasksList.get(position);
        rowView.setItemViewClickListener(position);
        rowView.setName(task.getName());
        rowView.setDescription(task.getDescription().replaceAll("[\n]", ""));
        rowView.setChecked(position, task.getIsExecuted() == 1);
        rowView.setDelete(position);
    }

    public void updateTasksList(List<Task> updatedList) {
        mTasksList = updatedList;
    }

    public void processItemClick(int position) {
        Task task = mTasksList.get(position);
        if (mListener != null) {
            mListener.onItemClick(task);
        }
    }

    public void processTaskStatusChanging(int position) {
        Task task = mTasksList.get(position);
        mTasksList.get(position).setIsExecuted(task.getIsExecuted() ^ 1);
        if (mListener != null) {
            mListener.onChangeItemExecuted(task);
        }
    }

    public void processItemDelete(int position) {
        Task task = mTasksList.get(position);
        mTasksList.remove(task);
        if (mListener != null) {
            mListener.onItemDelete(task);
        }
    }

    public int getTasksRowsCount() {
        return mTasksList.size();
    }

    public void unsubscribe() {
        mListener = null;
    }

    public interface OnItemClickListener {
        void onItemDelete(Task task);

        void onItemClick(Task task);

        void onChangeItemExecuted(Task task);
    }
}
