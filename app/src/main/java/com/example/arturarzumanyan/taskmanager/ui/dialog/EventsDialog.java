package com.example.arturarzumanyan.taskmanager.ui.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.ColorPalette;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import petrov.kristiyan.colorpicker.ColorPicker;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.EVENTS_KEY;

public class EventsDialog extends AppCompatDialogFragment {
    private static final int DEFAULT_COLOR = 9;
    private EditText mEditTextEventName, mEditTextEventDescription;
    private ImageButton mImageButtonColorPicker;
    private TextView mTextViewStartTime, mTextViewEndTime, mTextViewDate;
    private Switch mSwitchNotification;

    private EventsReadyListener eventsReadyListener;
    private EventsRepository mEventsRepository;

    private SparseIntArray mColorMap;

    private Date mStartTime;
    private Date mEndTime;

    private int mCurrentColor;
    private int mHour, mMinute;
    private int mDay, mMonth, mYear;

    public EventsDialog() {
    }

    public static EventsDialog newInstance(Event event) {
        EventsDialog eventsDialog = new EventsDialog();
        if (event != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EVENTS_KEY, event);
            eventsDialog.setArguments(bundle);
        }
        return eventsDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_events, null);

        final Bundle bundle = getArguments();

        ColorPalette colorPalette = new ColorPalette(getActivity());
        mColorMap = colorPalette.getColorPalette();

        mCurrentColor = mColorMap.get(DEFAULT_COLOR);

        mEventsRepository = new EventsRepository();

        builder.setView(view)
                .setTitle(getString(R.string.events_title))
                .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addOrUpdateEvent(bundle);
                    }
                });

        setViews(view);

        setTimeAndDatePickersListeners();

        if (bundle != null) {
            setEventInfoViews(bundle);
        }

        return builder.create();
    }

    private void setViews(View view) {
        mHour = DateUtils.getHour();
        mMinute = DateUtils.getMinute();

        mDay = DateUtils.getDay();
        mMonth = DateUtils.getMonth();
        mYear = DateUtils.getYear();

        mStartTime = new Date(0, 0, 0, mHour, mMinute);
        mEndTime = new Date(0, 0, 0, mHour + 1, mMinute);

        mEditTextEventName = view.findViewById(R.id.edit_text_event_name);
        mEditTextEventDescription = view.findViewById(R.id.edit_text_event_description);
        mImageButtonColorPicker = view.findViewById(R.id.image_button_color_picker);
        mTextViewStartTime = view.findViewById(R.id.text_start_time);
        mTextViewEndTime = view.findViewById(R.id.text_end_time);
        mTextViewDate = view.findViewById(R.id.text_event_date);
        mSwitchNotification = view.findViewById(R.id.switch_notification);

        mTextViewStartTime.setText(mHour + ":" + mMinute);
        mTextViewEndTime.setText((mHour + 1) + ":" + mMinute);
        mTextViewDate.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);

        mImageButtonColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

    }

    private void addOrUpdateEvent(Bundle bundle) {
        String name = mEditTextEventName.getText().toString();
        String description = mEditTextEventDescription.getText().toString();
        Date startDate = DateUtils.getEventDate(mTextViewDate.getText().toString(), mStartTime);
        Date endDate = DateUtils.getEventDate(mTextViewDate.getText().toString(), mEndTime);
        int isNotify = mSwitchNotification.isChecked() ? 1 : 0;

        int colorNumber = mColorMap.keyAt(mColorMap.indexOfValue(mCurrentColor));

        if (endDate != null) {
            if (endDate.after(startDate) && !mEditTextEventName.getText().toString().isEmpty()) {
                if (bundle != null) {
                    updateEvent(bundle, name, description, colorNumber, startDate, endDate, isNotify);
                } else {
                    addEvent(name, description, colorNumber, startDate, endDate, isNotify);
                }
            } else {
                Toast.makeText(getContext(), R.string.time_error_msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addEvent(String name, String description, int colorNumber, Date startDate,
                          Date endDate, int isNotify) {
        Event event = createEventObject(UUID.randomUUID().toString(), name, description,
                colorNumber, startDate, endDate, isNotify);
        mEventsRepository.addOrUpdateEvent(event, POST,
                new EventsRepository.OnEventsLoadedListener() {
                    @Override
                    public void onSuccess(List<Event> eventsList) {
                        eventsReadyListener.onEventsReady(eventsList);
                    }

                    @Override
                    public void onFail(String message) {
                        ((BaseActivity) requireActivity()).onError(message);
                    }
                });

    }

    private void updateEvent(Bundle bundle, String name, String description, int colorNumber,
                             Date startDate, Date endDate, int isNotify) {
        Event event = bundle.getParcelable(EVENTS_KEY);
        if (event != null) {
            event = createEventObject(event.getId(), name, description, colorNumber, startDate, endDate, isNotify);

            mEventsRepository.addOrUpdateEvent(event, PATCH, new EventsRepository.OnEventsLoadedListener() {
                @Override
                public void onSuccess(List<Event> eventsList) {
                    eventsReadyListener.onEventsReady(eventsList);
                }

                @Override
                public void onFail(String message) {
                    ((BaseActivity) requireActivity()).onError(message);
                }
            });
        }
    }

    private Event createEventObject(String id, String name, String description, int colorNumber,
                                    Date startDate, Date endDate, int isNotify) {
        return new Event(id, name, description, colorNumber, startDate, endDate, isNotify);
    }

    private void setTimeAndDatePickersListeners() {
        mTextViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mTextViewStartTime.setText(hourOfDay + ":" + minute);
                        mStartTime = new Date(0, 0, 0, hourOfDay, minute);
                    }
                }, mHour, mMinute, true).show();
            }
        });

        mTextViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mTextViewEndTime.setText(hourOfDay + ":" + minute);
                        mEndTime = new Date(0, 0, 0, hourOfDay, minute);
                    }
                }, mHour, mMinute, true).show();
            }
        });

        mTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mTextViewDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
    }

    private void setEventInfoViews(Bundle bundle) {
        Event event = bundle.getParcelable(EVENTS_KEY);
        if (event != null) {
            mEditTextEventName.setText(event.getName());
            mEditTextEventDescription.setText(event.getDescription());

            mImageButtonColorPicker.setColorFilter(mColorMap.get(event.getColorId()));
            mCurrentColor = mColorMap.get(event.getColorId());

            mTextViewStartTime.setText(DateUtils.formatTimeWithoutA(event.getStartTime()));
            mTextViewEndTime.setText(DateUtils.formatTimeWithoutA(event.getEndTime()));
            mTextViewDate.setText((DateUtils.formatEventDate(event.getStartTime())));

            mSwitchNotification.setChecked(event.getIsNotify() == 1);

            mStartTime = DateUtils.getTimeWithoutA(DateUtils.formatTimeWithoutA(event.getStartTime()));
            mEndTime = DateUtils.getTimeWithoutA(DateUtils.formatTimeWithoutA(event.getEndTime()));
        }
    }

    private void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(requireActivity());
        ArrayList<String> colors = new ArrayList<>();

        for (int i = 0; i < mColorMap.size(); i++) {
            colors.add("#" + Integer.toHexString(mColorMap.valueAt(i)));
        }

        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        mImageButtonColorPicker.setColorFilter(color);
                        mCurrentColor = color;
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();

    }

    public void setEventsReadyListener(EventsReadyListener listener) {
        this.eventsReadyListener = listener;
    }

    public interface EventsReadyListener {
        void onEventsReady(List<Event> events);
    }
}
