package com.bignerdranch.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {
    private static final String EXTRA_DATE = "date";

    public Intent newIntent(Context context, Date date){
        Intent intent = new Intent(context, DatePickerFragment.class);
        intent.putExtra(EXTRA_DATE, date);

        return intent;

    }

    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getExtras().getSerializable(EXTRA_DATE);

        DatePickerFragment fragment = DatePickerFragment.newInstance(date);

        return fragment;
    }
}
