package com.bignerdranch.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.criminalintent.database.CrimeDbScherma.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrime;

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public void addCrime(Crime c){
        ContentValues contentValues = getContentValues(c);

        mSQLiteDatabase.insert(CrimeTable.NAME, null, contentValues);

    }

    public static CrimeLab get(Context context){
        if(sCrime == null){
            sCrime = new CrimeLab(context);
        }

        return sCrime;
    }

    public Cursor getQueryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mSQLiteDatabase.query(CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);

        return cursor;
    }

    public List<Crime> getCrimes(){
        return new ArrayList<>();
    }

    private ContentValues getContentValues(Crime crime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, crime.getDate().toString());
        contentValues.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1:0);

        return contentValues;
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues contentValues = getContentValues(crime);

        mSQLiteDatabase.update(CrimeTable.NAME, contentValues,
                CrimeTable.Cols.UUID + "= ?", new String[]{uuidString});

    }

    public Crime getCrime(UUID uuid){

        return null;
    }

}
