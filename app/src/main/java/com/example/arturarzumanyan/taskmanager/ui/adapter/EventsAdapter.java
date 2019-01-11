package com.example.arturarzumanyan.taskmanager.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.HashMap;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements View.OnClickListener {
    private List<Event> mDataset;
    private EventsAdapter.OnItemClickListener mListener;
    private Context mContext;

    public EventsAdapter(Context context, List<Event> dataset, EventsAdapter.OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.mDataset = dataset;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.ViewHolder holder, int position) {
        Event event = mDataset.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int position = (int) v.getTag();
                    if (position != RecyclerView.NO_POSITION) {
                        Event event = mDataset.get(position);
                        mListener.onItemClick(event);
                        notifyItemChanged(position);
                        notifyItemRangeChanged(position, mDataset.size());
                    }
                }
            }
        });
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription().replaceAll("[\n]", ""));

        holder.eventDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    int position = (int) v.getTag();
                    if (position != RecyclerView.NO_POSITION) {
                        Event event = mDataset.get(position);
                        mListener.onItemDelete(event);
                        mDataset.remove(event);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mDataset.size());
                    }
                }
            }
        });
        holder.eventDelete.setTag(position);
        holder.itemView.setTag(position);

        ColorPalette colorPalette = new ColorPalette(mContext);
        HashMap<Integer, Integer> map = colorPalette.getColorPalette();
        holder.constraintLayout.setBackgroundColor(map.get(event.getColorId()));

        holder.eventTime.setText(DateUtils.formatTime(event.getStartTime())
                + " - " + DateUtils.formatTime(event.getEndTime()));
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            if (position != RecyclerView.NO_POSITION) {
                Event event = mDataset.get(position);
                mListener.onItemDelete(event);
                mDataset.remove(event);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateList(List<Event> updatedList) {
        mDataset = updatedList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ConstraintLayout constraintLayout;
        TextView eventName, eventDescription, eventTime;
        ImageView eventDelete;

        ViewHolder(View view) {
            super(view);
            mView = view;
            constraintLayout = view.findViewById(R.id.constraint_layout_events_holder);
            eventName = view.findViewById(R.id.text_event_name);
            eventDescription = view.findViewById(R.id.text_event_description);
            eventDelete = view.findViewById(R.id.imageViewDeleteEvent);
            eventTime = view.findViewById(R.id.text_event_time);
        }
    }

    public interface OnItemClickListener {
        void onItemDelete(Event event);

        void onItemClick(Event event);
    }
}