package com.bluekeroro.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.UUID;

/**
 * Created by BlueKeroro on 2018/4/2.
 */
public class DatePickerActivity extends SingleFragmentActivity  {
    private static final String EXTRA_CRIME_DATE="com.bluekeroro.android.criminalintent.crime_date";
    @Override
    protected Fragment createFragment() {
        Date date=(Date)getIntent().getSerializableExtra(EXTRA_CRIME_DATE);
        return DatePickerFragment.newInstance(date);
    }

    public static Intent newIntent(Context packageContext, Date date){
        Intent intent=new Intent(packageContext, DatePickerActivity.class);
        intent.putExtra(EXTRA_CRIME_DATE,date);
        return intent;
    }

}
