package com.example.arturarzumanyan.taskmanager.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> implements View.OnClickListener {
    private List<Task> mDataset;
    private OnItemClickListener mListener;

    public TasksAdapter(List<Task> dataset, OnItemClickListener onItemClickListener) {
        this.mDataset = dataset;
        this.mListener = onItemClickListener;
    }

    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mDataset.get(position);
        holder.taskName.setText(task.getName());
        holder.taskDescription.setText(task.getDescription().replaceAll("[\n]", ""));

        //holder.taskDelete.setOnClickListener(this);
        holder.taskDelete.setTag(position);
        holder.isExecutedCheckBox.setChecked(task.isExecuted());
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            if (position != RecyclerView.NO_POSITION) {
                Task task = mDataset.get(position);
                mListener.onItemDelete(task);
                mDataset.remove(task);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateList(List<Task> updatedList) {
        mDataset = updatedList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView taskName, taskDescription;
        ImageView taskDelete;
        CheckBox isExecutedCheckBox;

        public ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            mView = view;
            taskName = view.findViewById(R.id.textViewTaskName);
            taskDescription = view.findViewById(R.id.textViewTaskDescription);
            taskDelete = view.findViewById(R.id.imageViewDelete);
            isExecutedCheckBox = view.findViewById(R.id.checkBoxIsExecuted);
        }
    }

    public interface OnItemClickListener {
        void onItemDelete(Task task);
    }
}
