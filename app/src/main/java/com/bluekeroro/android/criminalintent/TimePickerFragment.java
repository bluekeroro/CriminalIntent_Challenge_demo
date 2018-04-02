package com.bluekeroro.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by BlueKeroro on 2018/4/1.
 */
public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME="com.bluekeroro.android.criminalintent.time";
    private static final String ARG_TIME="time";
    private TimePicker mTimePicker;
    public static TimePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_TIME,date);
        TimePickerFragment fragment=new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date=(Date)getArguments().getSerializable(ARG_TIME);
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);
        mTimePicker=(TimePicker)v.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour=mTimePicker.getCurrentHour();
                                int minute=mTimePicker.getCurrentMinute();
                                Date date=new GregorianCalendar(year,month,day,hour,minute).getTime();
                                sendResult(Activity.RESULT_OK,date);
                            }
                        })
                .create();
    }
    private void sendResult(int resultCode,Date date){
        if(getTargetFragment()==null){
            return ;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_TIME,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
