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
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.adapter.ColorPalette;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import petrov.kristiyan.colorpicker.ColorPicker;

public class EventsDialog extends AppCompatDialogFragment {
    private EditText mEditTextEventName, mEditTextEventDescription;
    private ImageButton mImageButtonColorPicker;
    private TextView mTextViewStartTime, mTextViewEndTime, mTextViewDate;
    private Switch mSwitchNotification;

    private Date mStartTime;
    private Date mEndTime;

    private int mHour, mMinute;
    private int mDay, mMonth, mYear;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_events, null);

        builder.setView(view)
                .setTitle("Events")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mStartTime.getTime() < mEndTime.getTime()) {

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
        mImageButtonColorPicker = view.findViewById(R.id.imageButtonColorPicker);
        mTextViewStartTime = view.findViewById(R.id.textViewStartTime);
        mTextViewEndTime = view.findViewById(R.id.textViewEndTime);
        mTextViewDate = view.findViewById(R.id.textViewEventDate);
        mSwitchNotification = view.findViewById(R.id.switchNotification);

        mTextViewStartTime.setText(mHour + ":" + mMinute);
        mTextViewEndTime.setText((mHour + 1) + ":" + mMinute);
        mTextViewDate.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);

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
                        mTextViewDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        return builder.create();
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
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }

    public interface EventsDialogListener {
        void onButtonCreateClicked();

        void onButtonUpdateClicked();
    }
}
