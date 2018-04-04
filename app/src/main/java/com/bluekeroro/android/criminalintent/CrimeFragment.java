package com.bluekeroro.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by BlueKeroro on 2018/3/27.
 */
public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private static final String ARG_CRIME_ID="crime_id";
    private static final String DIALOG_TIME="DialogTIME";
    private static final String DIALOG_PHOTO="DialogPHOTO";
    private static final int REQUEST_CONTACT=1;
    private static final int REQUEST_PHOTO=2;
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_TIME=3;
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCrime=new Crime();
        UUID crimeId=(UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime=CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile=CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_crime,container,false);
        mTitleField=(EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
                //returnResult();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This one too
            }
        });
        mDateButton=(Button)v.findViewById(R.id.crime_date);
        //mDateButton.setText(mCrime.getDate().toString());
        mTimeButton=(Button)v.findViewById(R.id.crime_time);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onDateButtonClick(CrimeFragment.this,mCrime.getDate(),REQUEST_DATE);
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getFragmentManager();
                TimePickerFragment dialog=TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(manager,DIALOG_TIME);
            }
        });
        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });
        mReportButton=(Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder intentBuilder=ShareCompat.IntentBuilder.from(getActivity());
                intentBuilder.setChooserTitle(getString(R.string.send_report));
                intentBuilder.setType("text/plain");
                intentBuilder.setText(getCrimeReport());
                intentBuilder.setSubject(getString(R.string.crime_report_subject));
                intentBuilder.startChooser();
            }
        });
        final Intent pickContact=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton=(Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });
        if(mCrime.getSuspect()!=null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        PackageManager packageManager=getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY)==null){
            mSuspectButton.setEnabled(false);
        }
        mCallButton=(Button)v.findViewById(R.id.crime_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCrime.getSuspect()==null){
                    return;
                }
                Uri number;
                Cursor c=getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI
                        ,null
                        ,ContactsContract.Contacts.DISPLAY_NAME+"=?"
                        ,new String[]{mCrime.getSuspect()}
                        ,null);
                c.moveToFirst();
                String id=c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone=getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                        ,null
                        ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?"
                        ,new String[]{id}
                        ,null);
                c.close();
                try{
                    if(phone.getCount()==0){
                        return ;
                    }
                    phone.moveToFirst();
                    number=Uri.parse("tel:"+phone.getString(phone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                }finally {
                    phone.close();
                }
                Intent callContact=new Intent(Intent.ACTION_DIAL,number);
                startActivity(callContact);
            }
        });
        mPhotoView=(ImageView)v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getFragmentManager();
                PhotoDialogFragment dialog=PhotoDialogFragment.newInstance(mCrime.getId());
                dialog.setTargetFragment(CrimeFragment.this,0);
                dialog.show(manager,DIALOG_PHOTO);
            }
        });
        ViewTreeObserver observer=mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(mPhotoFile==null||!mPhotoFile.exists()){
                    mPhotoView.setImageDrawable(null);
                }else{
                    Bitmap bitmap=PictureUtiles.getScaledBitmap(mPhotoFile.getPath(),mPhotoView.getWidth(),mPhotoView.getHeight());
                    mPhotoView.setImageBitmap(bitmap);
                }
            }
        });
        mPhotoButton=(ImageButton)v.findViewById(R.id.crime_camera);
        final Intent captureImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto=mPhotoFile!=null&&captureImage.resolveActivity(packageManager)!=null;
        mPhotoButton.setEnabled(canTakePhoto);
        if(canTakePhoto){
            Uri uri=Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=Activity.RESULT_OK){
            return ;
        }
        if(requestCode==REQUEST_DATE){
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }else if(requestCode==REQUEST_TIME){
            Date date=(Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }else if(requestCode==REQUEST_CONTACT&&data!=null){
            Uri contactUri=data.getData();
            // Specify which fields you want your query to return value for.
            String[] queryFields=new String[]{
                ContactsContract.Contacts.DISPLAY_NAME
            };
            //Perform your query - the contactUri is like a "where" clause here.
            Cursor c=getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            try{
                // Double-check that you actually got results
                if(c.getCount()==0){
                    return ;
                }
                // Pull out the first column of the first row of data -that is your suspect's name.
                c.moveToFirst();
                String suspect=c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }else if(requestCode==REQUEST_PHOTO){
            updateCrime();
            updatePhotoView();
        }
    }

    private void updateDate() {
        //mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setText(new DateFormat().format("EEEE,MMM dd,yyyy",mCrime.getDate()));
        mTimeButton.setText(new DateFormat().format("k:mm",mCrime.getDate()));
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    public String getCrimeReport(){
        String solvedString=null;
        if(mCrime.isSolved()){
            solvedString=getString(R.string.crime_report_solved);
        }else{
            solvedString=getString(R.string.crime_report_unsolved);
        }
        String dateFormat="EEE,MMM dd";
        String dateString=DateFormat.format(dateFormat,mCrime.getDate()).toString();
        String suspect=mCrime.getSuspect();
        if(suspect==null){
            suspect=getString(R.string.crime_report_no_suspect);
        }else{
            suspect=getString(R.string.crime_report_suspect,suspect);
        }
        String report=getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }

    private void updatePhotoView(){
        if(mPhotoFile==null||!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap=PictureUtiles.getScaledBitmap(mPhotoFile.getPath(),mPhotoView.getWidth(),mPhotoView.getHeight());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks{
        void onCrimeUpdate(int position);
        void onDateButtonClick(Fragment fragment,Date date,int request);
        void onDeleteButtonClick(Fragment fragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks=(Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }
    private void updateCrime(){
        //===================
        int mPosition=CrimeLab.get(getActivity()).getCrimePositionPlus1(mCrime);
        //===================
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdate(mPosition);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_delete:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                mCallbacks.onCrimeUpdate(0);
                mCallbacks.onDeleteButtonClick(this);
                //getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
