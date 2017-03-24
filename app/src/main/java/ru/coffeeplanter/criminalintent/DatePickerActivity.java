package ru.coffeeplanter.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        Intent intent = getIntent();
        Date date = (Date) intent.getSerializableExtra(CrimeFragment.DIALOG_DATE);
        return DatePickerFragment.newInstance(date);

    }

}
