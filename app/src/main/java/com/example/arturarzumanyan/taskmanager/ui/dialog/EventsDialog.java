package com.example.arturarzumanyan.taskmanager.ui.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatDialogFragment;
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
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.adapter.ColorPalette;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import petrov.kristiyan.colorpicker.ColorPicker;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.EVENTS_KEY;

public class EventsDialog extends AppCompatDialogFragment {
    private static final int CURRENT_COLOR = 9;
    private EditText mEditTextEventName, mEditTextEventDescription;
    private ImageButton mImageButtonColorPicker;
    private TextView mTextViewStartTime, mTextViewEndTime, mTextViewDate;
    private Switch mSwitchNotification;

    private EventsRepository mEventsRepository;

    private ColorPalette mColorPalette;

    private Date mStartTime;
    private Date mEndTime;

    private int mCurrentColor;
    private int mHour, mMinute;
    private int mDay, mMonth, mYear;

    public EventsDialog() {
        this.eventsReadyListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_events, null);

        final Bundle bundle = getArguments();

        mColorPalette = new ColorPalette(getActivity());
        for (HashMap.Entry<Integer, Integer> map : mColorPalette.getColorPalette().entrySet()) {
            if (map.getKey() == CURRENT_COLOR) {
                mCurrentColor = map.getValue();
            }
        }

        mEventsRepository = new EventsRepository(getActivity());

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
                        String name = mEditTextEventName.getText().toString();
                        String description = mEditTextEventDescription.getText().toString();
                        Date startDate = DateUtils.getEventDate(mTextViewDate.getText().toString(), mStartTime);
                        Date endDate = DateUtils.getEventDate(mTextViewDate.getText().toString(), mEndTime);
                        int isNotify;
                        if (mSwitchNotification.isChecked()) {
                            isNotify = 1;
                        } else {
                            isNotify = 0;
                        }

                        int colorNumber = CURRENT_COLOR;

                        for (HashMap.Entry<Integer, Integer> map : mColorPalette.getColorPalette().entrySet()) {
                            if (map.getValue() == mCurrentColor) {
                                colorNumber = map.getKey();
                            }
                        }

                        if (mStartTime.getDate() < mEndTime.getDate() &&
                                !mEditTextEventName.getText().toString().isEmpty() &&
                                bundle != null) {
                            Event event = bundle.getParcelable(EVENTS_KEY);
                            event.setName(name);
                            event.setDescription(description);
                            event.setColorId(colorNumber);
                            event.setStartTime(startDate);
                            event.setEndTime(endDate);
                            event.setIsNotify(isNotify);

                            mEventsRepository.updateEvent(event, new EventsCloudStore.OnTaskCompletedListener() {
                                @Override
                                public void onSuccess(ArrayList<Event> eventsList) {
                                    eventsReadyListener.onEventsReady(mEventsRepository.getDailyEvents());
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        } else if (mStartTime.getTime() < mEndTime.getTime() &&
                                !mEditTextEventName.getText().toString().isEmpty() &&
                                bundle == null) {

                            Event event = new Event(UUID.randomUUID().toString(),
                                    name,
                                    description,
                                    colorNumber,
                                    startDate,
                                    endDate,
                                    isNotify);
                            mEventsRepository.addEvent(event, new EventsCloudStore.OnTaskCompletedListener() {
                                @Override
                                public void onSuccess(ArrayList<Event> eventsList) {
                                    eventsReadyListener.onEventsReady(mEventsRepository.getDailyEvents());
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        } else {
                            Toast.makeText(getContext(),
                                    R.string.time_error_msg,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        mHour = DateUtils.getHour();
        mMinute = DateUtils.getMinute();

        mDay = DateUtils.getDay();
        mMonth = DateUtils.getMonth();
        mYear = DateUtils.getYear();

        mStartTime = new Date(0, 0, 0, mHour, mMinute);
        mEndTime = new Date(0, 0, 0, mHour + 1, mMinute);

        mEditTextEventName = view.findViewById(R.id.editTextEventName);
        mEditTextEventDescription = view.findViewById(R.id.editTextEventDescription);
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mTextViewDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        if (bundle != null) {
            setEventInfoViews(bundle);
        }
        return builder.create();
    }

    private void setEventInfoViews(Bundle bundle) {
        Event event = bundle.getParcelable(EVENTS_KEY);
        mEditTextEventName.setText(event.getName());
        mEditTextEventDescription.setText(event.getDescription());

        for (HashMap.Entry<Integer, Integer> map : mColorPalette.getColorPalette().entrySet()) {
            if (event.getColorId() == map.getKey()) {
                mImageButtonColorPicker.setColorFilter(map.getValue());
                mCurrentColor = map.getKey();
            }
        }

        mTextViewStartTime.setText(DateUtils.formatTimeWithoutA(event.getStartTime()));
        mTextViewEndTime.setText(DateUtils.formatTimeWithoutA(event.getEndTime()));
        mTextViewDate.setText((DateUtils.getEventDate(event.getStartTime())));

        if (event.getIsNotify() == 1) {
            mSwitchNotification.setChecked(true);
        } else {
            mSwitchNotification.setChecked(false);
        }

        mStartTime = DateUtils.getTimeWithoutA(DateUtils.formatTimeWithoutA(event.getStartTime()));
        mEndTime = DateUtils.getTimeWithoutA(DateUtils.formatTimeWithoutA(event.getEndTime()));
    }

    private void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(getActivity());
        ArrayList<String> colors = new ArrayList<>();
        ColorPalette colorPalette = new ColorPalette(getActivity());
        for (HashMap.Entry<Integer, Integer> map : colorPalette.getColorPalette().entrySet()) {
            colors.add("#" + Integer.toHexString(map.getValue()));
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

    public interface EventsReadyListener {
        void onEventsReady(ArrayList<Event> events);
    }

    public void setEventsReadyListener(EventsReadyListener listener) {
        this.eventsReadyListener = listener;
    }

    private EventsReadyListener eventsReadyListener;
}
