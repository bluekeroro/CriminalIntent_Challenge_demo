package com.bluekeroro.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by BlueKeroro on 2018/4/3.
 */
public class PhotoDialogFragment extends DialogFragment {
    private static final String CRIME_ID="crimeid";
    private ImageView mPhotoView;
    public static PhotoDialogFragment newInstance(UUID crimeId){
        Bundle args=new Bundle();
        args.putSerializable(CRIME_ID,crimeId);
        PhotoDialogFragment fragment=new PhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID crimeId=(UUID)getArguments().getSerializable(CRIME_ID);
        Crime crime=CrimeLab.get(getActivity()).getCrime(crimeId);
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo,null);
        mPhotoView=(ImageView)v.findViewById(R.id.dialog_photo_imageView);
        final File mPhotoFile=CrimeLab.get(getActivity()).getPhotoFile(crime);
        ViewTreeObserver observer=mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(mPhotoFile==null||!mPhotoFile.exists()){
                    mPhotoView.setImageDrawable(null);
                }else{
                    Bitmap bitmap=PictureUtiles.getScaledBitmap(mPhotoFile.getPath(),getActivity());
                    mPhotoView.setImageBitmap(bitmap);
                }
            }
        });

       /* if(mPhotoFile==null||!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Log.d("PhotoDialogFragment",mPhotoView.getWidth()+"/"+mPhotoView.getHeight());
            Bitmap bitmap=PictureUtiles.getScaledBitmap(mPhotoFile.getPath(),mPhotoView.getWidth(),mPhotoView.getHeight());
            mPhotoView.setImageBitmap(bitmap);
        }*/
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.dialog_photo_title)
                .setPositiveButton(android.R.string.ok,null)
                .create();
    }
}
