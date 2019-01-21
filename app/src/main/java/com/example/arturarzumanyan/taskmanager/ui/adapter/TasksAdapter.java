package com.example.arturarzumanyan.taskmanager.ui.adapter;

import android.support.annotation.NonNull;
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

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private List<Task> mDataset;
    private OnItemClickListener mListener;

    public TasksAdapter(List<Task> dataset, OnItemClickListener onItemClickListener) {
        this.mDataset = dataset;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = mDataset.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerItemClick(v);
            }
        });
        holder.taskName.setText(task.getName());
        holder.taskDescription.setText(task.getDescription().replaceAll("[\n]", ""));

        holder.taskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(v);
            }
        });

        holder.isExecutedCheckBox.setChecked(task.getIsExecuted() == 1);

        holder.isExecutedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTaskStatus(v);
            }
        });

        holder.taskDelete.setTag(position);
        holder.itemView.setTag(position);
        holder.isExecutedCheckBox.setTag(position);
    }

    private void triggerItemClick(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            if (position != RecyclerView.NO_POSITION) {
                Task task = mDataset.get(position);
                mListener.onItemClick(task);
            }
        }
    }

    private void changeTaskStatus(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            if (position != RecyclerView.NO_POSITION) {
                Task task = mDataset.get(position);
                setItemChecked(task, position, task.getIsExecuted() ^ 1);
                notifyItemChanged(position);
            }
        }
    }

    private void deleteItem(View v) {
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

    private void setItemChecked(Task task, int position, int checked) {
        mDataset.get(position).setIsExecuted(checked);
        task.setIsExecuted(checked);
        mListener.onChangeItemExecuted(task);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateList(List<Task> updatedList) {
        mDataset = updatedList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView taskDescription;
        ImageView taskDelete;
        CheckBox isExecutedCheckBox;

        ViewHolder(View view) {
            super(view);
            taskName = view.findViewById(R.id.text_task_name);
            taskDescription = view.findViewById(R.id.text_task_description);
            taskDelete = view.findViewById(R.id.image_delete);
            isExecutedCheckBox = view.findViewById(R.id.chb_is_executed);
        }
    }

    public interface OnItemClickListener {
        void onItemDelete(Task task);

        void onItemClick(Task task);

        void onChangeItemExecuted(Task task);
    }
}
