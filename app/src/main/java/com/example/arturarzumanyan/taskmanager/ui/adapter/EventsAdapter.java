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

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventsAdapter.ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        Event event = mDataset.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription().replaceAll("[\n]", ""));

        //holder.taskDelete.setOnClickListener(this);
        holder.eventDelete.setTag(position);

        ColorPalette colorPalette = new ColorPalette(mContext);
        HashMap<Integer, Integer> map = colorPalette.getColorPalette();
        holder.linearLayout.setBackgroundColor(map.get(event.getColorId()));

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