package ru.coffeeplanter.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_HOURS = "ru.coffeeplanter.criminalintent.hours";
    public static final String EXTRA_MINUTES = "ru.coffeeplanter.criminalintent.minutes";

    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("deprecation")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_TIME);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= 23 ) {
            mTimePicker.setHour(hours);
            mTimePicker.setMinute(minutes);
        }
        else {
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int newHours;
                        int newMinutes;
                        if (Build.VERSION.SDK_INT >= 23 ) {
                            newHours = mTimePicker.getHour();
                            newMinutes = mTimePicker.getMinute();
                        }
                        else {
                            newHours = mTimePicker.getCurrentHour();
                            newMinutes = mTimePicker.getCurrentMinute();
                        }

                        sendResult(Activity.RESULT_OK, newHours, newMinutes);
                    }
                })
                .create();

    }

    private void sendResult(int resultCode, int hours, int minutes) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_HOURS, hours);
        intent.putExtra(EXTRA_MINUTES, minutes);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
