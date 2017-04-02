package ru.coffeeplanter.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        Intent intent = getIntent();
        Date date = (Date) intent.getSerializableExtra(CrimeFragment.DIALOG_DATE);
        return DatePickerFragment.newInstance(date);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.date_picker_title);
        }
    }

}
