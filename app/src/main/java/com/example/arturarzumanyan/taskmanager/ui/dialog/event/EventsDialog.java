package com.example.arturarzumanyan.taskmanager.ui.dialog.event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.dialog.event.mvp.EventsDialogContract;
import com.example.arturarzumanyan.taskmanager.ui.dialog.event.mvp.EventsDialogPresenterImpl;

import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.EVENTS_KEY;

public class EventsDialog extends AppCompatDialogFragment implements EventsDialogContract.EventsDialogView {
    private EditText mEditTextEventName, mEditTextEventDescription;
    private ImageButton mImageButtonColorPicker;
    private TextView mTextViewStartTime, mTextViewEndTime, mTextViewDate;
    private Switch mSwitchNotification;

    private EventsReadyListener eventsReadyListener;
    private EventsDialogContract.EventsDialogPresenter mEventsDialogPresenter;

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

        mEventsDialogPresenter = new EventsDialogPresenterImpl(this);
        mEventsDialogPresenter.setDefaultCurrentColor(getActivity());

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
                        mEventsDialogPresenter.processOkButtonClick(bundle,
                                mEditTextEventName.getText().toString(),
                                mEditTextEventDescription.getText().toString(),
                                mTextViewDate.getText().toString(),
                                mSwitchNotification.isChecked() ? 1 : 0);
                    }
                });

        setViews(view);

        mEventsDialogPresenter.processReceivedBundle(bundle);

        return builder.create();
    }

    private void setViews(View view) {
        mEditTextEventName = view.findViewById(R.id.edit_text_event_name);
        mEditTextEventDescription = view.findViewById(R.id.edit_text_event_description);
        mImageButtonColorPicker = view.findViewById(R.id.image_button_color_picker);
        mTextViewStartTime = view.findViewById(R.id.text_start_time);
        mTextViewEndTime = view.findViewById(R.id.text_end_time);
        mTextViewDate = view.findViewById(R.id.text_event_date);
        mSwitchNotification = view.findViewById(R.id.switch_notification);

        mEventsDialogPresenter.setDefaultTimeValues();

        mImageButtonColorPicker.setColorFilter(requireActivity().getResources().getColor(R.color._9));

        mImageButtonColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventsDialogPresenter.processColorPicker();
            }
        });
    }

    @Override
    public void setDefaultTimeViews(int year, int month, int day, int hour, int minute) {
        mTextViewStartTime.setText(DateUtils.formatHourMinuteTime(hour, minute));
        mTextViewEndTime.setText(DateUtils.formatHourMinuteTime(hour + 1, minute));
        mTextViewDate.setText(DateUtils.getStringDateFromInt(year, month, day));
    }

    @Override
    public void onEventsReady(List<Event> events) {
        eventsReadyListener.onEventsReady(events);
    }

    @Override
    public void onWrongDataSetInViews() {
        Toast.makeText(getContext(), R.string.time_error_msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFail(String message) {
        ((BaseActivity) requireActivity()).onError(message);
    }

    @Override
    public void setTimeAndDatePickers(final int year, final int month, final int day, final int hour, final int minute) {
        mTextViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mEventsDialogPresenter.setEventStartTime(hourOfDay, minute);
                    }
                }, hour, minute, true).show();
            }
        });

        mTextViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mEventsDialogPresenter.setEventEndTime(hourOfDay, minute);
                    }
                }, hour, minute, true).show();
            }
        });

        mTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mEventsDialogPresenter.setEventDate(year, monthOfYear, dayOfMonth);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });
    }

    @Override
    public void setStartTimeView(int hour, int minute) {
        mTextViewStartTime.setText(DateUtils.formatHourMinuteTime(hour, minute));
    }

    @Override
    public void setEndTimeView(int hour, int minute) {
        mTextViewEndTime.setText(DateUtils.formatHourMinuteTime(hour, minute));
    }

    @Override
    public void setEventDateView(int year, int month, int day) {
        mTextViewDate.setText(DateUtils.getStringDateFromInt(year, month, day));
    }

    @Override
    public void setEventInfoViews(Event event) {
        mEditTextEventName.setText(event.getName());
        mEditTextEventDescription.setText(event.getDescription());

        mEventsDialogPresenter.setCurrentColor(event.getColorId());

        mTextViewStartTime.setText(DateUtils.formatTimeWithoutA(event.getStartTime()));
        mTextViewEndTime.setText(DateUtils.formatTimeWithoutA(event.getEndTime()));
        mTextViewDate.setText(DateUtils.formatReversedDayMonthYearDate(DateUtils.formatEventDate(event.getStartTime())));

        mSwitchNotification.setChecked(event.getIsNotify() == 1);

        mEventsDialogPresenter.setEventStartTime(event.getStartTime());
        mEventsDialogPresenter.setEventEndTime(event.getEndTime());
    }

    @Override
    public void setColorFilter(int colorId) {
        mImageButtonColorPicker.setColorFilter(colorId);
    }

    @Override
    public void showColorPicker(ArrayList<String> colors) {
        final ColorPicker colorPicker = new ColorPicker(requireActivity());
        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        mEventsDialogPresenter.setCurrentColor(position + 1);
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
