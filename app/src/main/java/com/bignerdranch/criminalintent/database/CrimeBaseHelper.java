package com.bignerdranch.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "crimeBase.db";

    /**
     *
     * @param context
     */
    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
