package com.example.arturarzumanyan.taskmanager.ui.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;

public class EventsDialog extends AppCompatDialogFragment {
    private EditText editTextEventName, editTextEventDescription;
    private ImageButton imageButtonColorPicker;
    private TextView textViewStartTime, textViewEndTime, textViewDate;
    private Switch switchNotification;

    private Date startTime;
    private Date endTime;

    private int hour, minute;
    private int day, month, year;

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
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(startTime.getTime() < endTime.getTime()){

                        }else{
                            Toast.makeText(getContext(),
                                    R.string.TimeErrorMsg,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        startTime = new Date(0, 0,0, hour, minute);
        endTime = new Date(0, 0,0, hour+1, minute);

        editTextEventName = view.findViewById(R.id.editTextEventName);
        editTextEventDescription = view.findViewById(R.id.editTextEventDescription);
        imageButtonColorPicker = view.findViewById(R.id.imageButtonColorPicker);
        textViewStartTime = view.findViewById(R.id.textViewStartTime);
        textViewEndTime = view.findViewById(R.id.textViewEndTime);
        textViewDate = view.findViewById(R.id.textViewEventDate);
        switchNotification = view.findViewById(R.id.switchNotification);

        textViewStartTime.setText(hour + ":" + minute);
        textViewEndTime.setText((hour+1) + ":" + minute);
        textViewDate.setText(day + "/" + (month+1) + "/" + year);

        imageButtonColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textViewStartTime.setText(hourOfDay + ":" + minute);
                        startTime = new Date(0, 0,0, hourOfDay, minute);
                    }
                }, hour, minute, true).show();
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textViewEndTime.setText(hourOfDay + ":" + minute);
                        endTime = new Date(0, 0,0, hourOfDay, minute);
                    }
                }, hour, minute, true).show();
            }
        });

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        textViewDate.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        return builder.create();
    }

    private void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(getActivity());
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._1, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._2, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._3, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._4, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._5, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._6, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._7, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._8, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._9, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._10, null)));
        colors.add("#" + Integer.toHexString(ResourcesCompat.getColor(getActivity().getResources(), R.color._11, null)));

        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        imageButtonColorPicker.setColorFilter(color);
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
