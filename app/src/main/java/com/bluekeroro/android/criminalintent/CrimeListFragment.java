package com.bluekeroro.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by BlueKeroro on 2018/3/27.
 */
public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static final int REQUEST_CRIME_POSITION=1;
    private static final String SAVED_SUBTITLE_VISIBLE="subtitle";
    private static int mPosition;
    private boolean mSubtitleVisible;
    private Callback mCallback;
    private Button mBlankNewCriem;
    private View mBlankView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_crime_list,container,false);
        mCrimeRecyclerView=(RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBlankView=(View)view.findViewById(R.id.crime_recycler_view_blank);
        mBlankNewCriem=(Button)view.findViewById(R.id.blank_new_crime);
        mBlankNewCriem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                mCallback.onCrimeSelected(crime,REQUEST_CRIME_POSITION);
            }
        });
        if(CrimeLab.get(getActivity()).getCrimes().size()==0){
            mBlankView.setVisibility(View.VISIBLE);
            mCrimeRecyclerView.setVisibility(View.INVISIBLE);
        }else{
            mBlankView.setVisibility(View.INVISIBLE);
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
        }
        if(savedInstanceState!=null){
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI(0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mSubtitleVisible){
            updateSubtitle();
        }
        updateUI(0);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;
        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView=(TextView)itemView.findViewById(R.id.list_time_crime_title_text_view);
            mDateTextView=(TextView)itemView.findViewById(R.id.list_time_crime_date_text_view);
            mSolvedCheckBox=(CheckBox)itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mSolvedCheckBox.setEnabled(false);
        }
        public void bindCrime(Crime crime){
            mCrime=crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(new DateFormat().format("k:mm,EEEE,MMM dd,yyyy,",mCrime.getDate()));
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v) {
            mCallback.onCrimeSelected(mCrime,REQUEST_CRIME_POSITION);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> Crimes){
            mCrimes=Crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            View view=layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime=mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes=crimes;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_CRIME_POSITION){
            mPosition=CrimePagerActivity.getCrimePosition(data);
        }
    }

    public void updateUI(int position){
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        List<Crime> crimes=crimeLab.getCrimes();
        if(CrimeLab.get(getActivity()).getCrimes().size()==0){
            mBlankView.setVisibility(View.VISIBLE);
            mCrimeRecyclerView.setVisibility(View.INVISIBLE);
        }else{
            mBlankView.setVisibility(View.INVISIBLE);
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
        }
        if(mAdapter==null){
            mAdapter=new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else if(position==0){
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(position-1);
        }
        updateSubtitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem=menu.findItem(R.id.menu_item_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI(0);
                mCallback.onCrimeSelected(crime,REQUEST_CRIME_POSITION);
                return true;
            case R.id.menu_item_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        int crimeCount=crimeLab.getCrimes().size();
        String subtitle=getResources().getQuantityString(R.plurals.subtitle_plural,crimeCount,crimeCount);
        if(!mSubtitleVisible){
            subtitle=null;
        }
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    /**
     * Required interface for hosting activities
     */
    public interface Callback{
        void onCrimeSelected(Crime crime,int request);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback=(Callback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback=null;
    }
}
