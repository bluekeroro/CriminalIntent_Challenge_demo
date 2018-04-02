package com.bluekeroro.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by BlueKeroro on 2018/3/28.
 */
public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks{
    private static final String EXTRA_CRIME_ID="com.bluekeroro.android.criminalintent.crime_id";
    private static final String EXTRA_CRIME_POSITION="com.bluekeroro.android.criminalintent.crime_position";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent=new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId=(UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager=(ViewPager)findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes=CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager){
            @Override
            public Fragment getItem(int position) {
                Crime crime=mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        for(int i=0;i<mCrimes.size();i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
        setCrimePositionResult(CrimeLab.get(this).getCrime(crimeId));
    }

    public static int getCrimePosition(Intent result){
        return result.getIntExtra(EXTRA_CRIME_POSITION,0);
    }
    private void setCrimePositionResult(Crime crime){
        int mPosition=CrimeLab.get(this).getCrimePositionPlus1(crime);
        Intent result=new Intent();
        result.putExtra(EXTRA_CRIME_POSITION,mPosition);
        setResult(RESULT_OK,result);
    }
    @Override
    public void onCrimeUpdate(int position) {

    }

    @Override
    public void onDateButtonClick(Fragment fragment,Date date, int request) {
        Intent intent=DatePickerActivity.newIntent(this,date);
        fragment.startActivityForResult(intent,request);
    }

    @Override
    public void onDeleteButtonClick(Fragment fragment) {
        this.finish();
    }
}
