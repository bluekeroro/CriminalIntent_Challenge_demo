package com.bluekeroro.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bluekeroro.android.criminalintent.database.CrimeBaseHelper;
import com.bluekeroro.android.criminalintent.database.CrimeDbSchema;
import com.bluekeroro.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by BlueKeroro on 2018/3/27.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab==null){
            sCrimeLab=new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context){
        mContext=context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();
    }
    public List<Crime> getCrimes(){
        List<Crime> crimes=new ArrayList<>();
        CrimeCursorWrapper cursor=queryCrime(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }
    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor=queryCrime(
                CrimeTable.Cols.UUID+"=?",
                new String[]{id.toString()});
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }
    public void addCrime(Crime c){
        ContentValues values=getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }
    public void deleteCrime(Crime c){
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID+"=?"
                ,new String[]{c.getId().toString()});
    }
    private static ContentValues getContentValues(Crime crime){
        ContentValues values=new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved()?1:0);
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());
        return values;
    }

    public void updateCrime(Crime crime){
        String uuidString=crime.getId().toString();
        ContentValues values=getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,
                CrimeTable.Cols.UUID+"=?",
                new String[]{uuidString});
    }
    private CrimeCursorWrapper queryCrime(String whereClause,String[] whereArgs){
        Cursor cursor=mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }
    public File getPhotoFile(Crime crim){
        File externalFileDire=mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFileDire==null){
            return null;
        }
        return new File(externalFileDire,crim.getPhotoFilename());
    }

    public int getCrimePositionPlus1(Crime crime){
        int mPosition=1;
        List<Crime> crimes=new ArrayList<>();
        CrimeCursorWrapper cursor=queryCrime(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                if(cursor.getCrime().getId().equals(crime.getId())){
                    return mPosition;
                }
                cursor.moveToNext();
                mPosition++;
            }
        }finally {
            cursor.close();
        }
        return 0;
    }
}
