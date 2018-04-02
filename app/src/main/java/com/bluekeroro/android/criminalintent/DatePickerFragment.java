package com.bluekeroro.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by BlueKeroro on 2018/3/29.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE="com.bluekeroro.android.criminalintent.date";
    private static final String ARG_DATE="date";
    private DatePicker mDatePicker;
    private Button mOkButton;
    public static DatePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);
        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_date,container,false);
        Date date=(Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker=(DatePicker)view.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year,month,day,null);
        mOkButton=(Button)view.findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year=mDatePicker.getYear();
                int month=mDatePicker.getMonth();
                int day=mDatePicker.getDayOfMonth();
                Date date=new GregorianCalendar(year,month,day).getTime();
                sendResult(Activity.RESULT_OK,date);
            }
        });
        return view;
    }

    /*@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date=(Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);
        mDatePicker=(DatePicker)v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year,month,day,null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year=mDatePicker.getYear();
                                int month=mDatePicker.getMonth();
                                int day=mDatePicker.getDayOfMonth();
                                Date date=new GregorianCalendar(year,month,day).getTime();
                                sendResult(Activity.RESULT_OK,date);
                            }
                        })
                .create();
    }*/
    private void sendResult(int resultCode,Date date){
        Intent data=new Intent();
        data.putExtra(EXTRA_DATE,date);
        if(getTargetFragment()==null){
            getActivity().setResult(resultCode,data);
            getActivity().finish();
        }else{
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,data);
            dismiss();
        }
    }
    /*public static Date showResult(Intent data){
        return (Date)data.getSerializableExtra(EXTRA_DATE);
    }*/
}
