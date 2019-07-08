package com.bignerdranch.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mReportButton;
    private Button mContactButton;
    private Button mCallButton;
    private CheckBox mSolvedCheckBox;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    public static final int REQUEST_CONTACT = 1;

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;

    public static CrimeFragment newInstance(UUID uuid){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, uuid);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);

            }
        });

        mReportButton = view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);*/

                ShareCompat.IntentBuilder intentBuilder =
                        ShareCompat.IntentBuilder.from(getActivity());
                intentBuilder.setText(getCrimeReport());
                intentBuilder.setChooserTitle(getString(R.string.send_report));
                intentBuilder.setSubject(getString(R.string.crime_report_subject));
                intentBuilder.setType("text/plain");
                Intent i =  intentBuilder.createChooserIntent();
                startActivity(i);
            }
        });

        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        /**
         * Working on this, trying to get the phone number from the contact picked as a suspect
         */
        mCallButton = view.findViewById(R.id.crime_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if((ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)){

                    Cursor c = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
                            new String[]{mCrime.getSuspect()},
                            null);
                    c.moveToFirst();
                    String phoneNumber = "tel:" + c.getString(1);
                    Uri uriPhoneNumber = Uri.parse(phoneNumber);

                   //Intent intent = new Intent(Intent.ACTION_CALL);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(uriPhoneNumber);
                    startActivity(intent);
                }else{
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }


            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContactButton = view.findViewById(R.id.crime_suspect);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        updateContactButton();

        //What is this?
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mContactButton.setEnabled(false);
        }

        return view;
    }

    private void updateContactButton() {
        if (mCrime.getSuspect() != null) {
            mContactButton.setText(mCrime.getSuspect());
            mCallButton.setEnabled(true);
        } else {
            mCallButton.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getExtras().getSerializable(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if(requestCode == REQUEST_CONTACT && data != null){
            Uri uri = data.getData();
            String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME};

            Cursor c = getActivity().getContentResolver().query(uri, queryFields,
                    null, null, null);
            try{
                if(c.getCount() == 0){
                    return;
                }

                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateContactButton();

            } finally {
                c.close();
            }

        }

    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)
                .format(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        } else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
