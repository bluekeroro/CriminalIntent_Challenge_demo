package com.bluekeroro.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;

import java.util.Date;

/**
 * Created by BlueKeroro on 2018/3/27.
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callback,CrimeFragment.Callbacks{
    private static final String DIALOG_DATE="DialogDate";
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime,int request) {
        if(findViewById(R.id.detail_fragment_container)==null){
            Intent intent=CrimePagerActivity.newIntent(this,crime.getId());
            getSupportFragmentManager().findFragmentById(R.id.fragment_container)
            .startActivityForResult(intent,request);
        }else{
            Fragment newDetail=CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdate(int position) {
        CrimeListFragment listFragment=(CrimeListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI(position);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onDateButtonClick(Fragment fragment,Date date, int request) {
        FragmentManager manager=getSupportFragmentManager();
        DatePickerFragment dialog=DatePickerFragment.newInstance(date);
        dialog.setTargetFragment(fragment,request);
        dialog.show(manager,DIALOG_DATE);
    }

    @Override
    public void onDeleteButtonClick(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
