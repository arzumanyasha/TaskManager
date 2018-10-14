package com.example.arturarzumanyan.taskmanager.ui.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements View.OnClickListener {
    private List<Event> dataset;
    private EventsAdapter.OnItemClickListener mListener;
    private Context mContext;

    public EventsAdapter(Context context, List<Event> dataset, EventsAdapter.OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.dataset = dataset;
        this.mListener = onItemClickListener;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventsAdapter.ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        Event event = dataset.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription().replaceAll("[\n]", ""));

        //holder.taskDelete.setOnClickListener(this);
        holder.eventDelete.setTag(position);
        switch (event.getColorId()) {
            case 1:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._1, null));
                break;
            case 2:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._2, null));
                break;
            case 3:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._3, null));
                break;
            case 4:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._4, null));
                break;
            case 5:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._5, null));
                break;
            case 6:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._6, null));
                break;
            case 7:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._7, null));
                break;
            case 8:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._8, null));
                break;
            case 9:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._9, null));
                break;
            case 10:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._10, null));
                break;
            case 11:
                holder.linearLayout.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color._11, null));
                break;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a");
        holder.eventTime.setText(simpleDateFormat.format(event.getStartTime())
                + " - " + simpleDateFormat.format(event.getEndTime()));
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            if (position != RecyclerView.NO_POSITION) {
                Event event = dataset.get(position);
                mListener.onItemDelete(event);
                dataset.remove(event);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, dataset.size());
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void updateList(List<Event> updatedList) {
        dataset = updatedList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        LinearLayout linearLayout;
        TextView eventName, eventDescription, eventTime;
        ImageView eventDelete;

        public ViewHolder(View view, final EventsAdapter.OnItemClickListener listener) {
            super(view);
            mView = view;
            linearLayout = view.findViewById(R.id.linearLayoutEvents);
            eventName = view.findViewById(R.id.textViewEventName);
            eventDescription = view.findViewById(R.id.textViewEventDescription);
            eventDelete = view.findViewById(R.id.imageViewDeleteEvent);
            eventTime = view.findViewById(R.id.textViewEventTime);
        }
    }

    public interface OnItemClickListener {
        void onItemDelete(Event event);
    }
}