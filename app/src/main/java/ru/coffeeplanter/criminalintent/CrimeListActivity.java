package ru.coffeeplanter.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            intent.putExtra(
                    CrimeListFragment.SAVED_SUBTITLE_VISIBLE,
                    ((CrimeListFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container)).mSubtitleVisible
            );
            startActivityForResult(intent, CrimeListFragment.START_CRIME_DETAILS);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail, "CrimeFragment")
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .detach(newDetail)
                    .commitNowAllowingStateLoss();
            getSupportFragmentManager().beginTransaction()
                    .attach(newDetail)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

}
