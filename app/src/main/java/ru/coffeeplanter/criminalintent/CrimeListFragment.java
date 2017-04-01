package ru.coffeeplanter.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {

    static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    static final int START_CRIME_DETAILS = 0;

    private int mLastClickedPosition = RecyclerView.NO_POSITION;

    RecyclerView mCrimeRecyclerView;
    private LinearLayout mEmptyView;
    private Button mAddCrime;
    public CrimeAdapter mAdapter;
    boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    /**
     *
     * Обязательный интерфейс для активности-хоста
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            mSubtitleVisible = bundle.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        mEmptyView = (LinearLayout) view.findViewById(R.id.empty_view);
        mAddCrime = (Button) view.findViewById(R.id.add_crime_button);
        mAddCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewCrime();
            }
        });
        updateUI();
        return view;
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else if (mLastClickedPosition != RecyclerView.NO_POSITION) {
            mAdapter.setCrimes(crimes);
            // Отключил, т. к. были проблемы с обновлением при удалении преступления,
            // Возможно, нужно заменить на колбэк при удалении преступления в CrimeFragment
//            mAdapter.notifyItemChanged(mLastClickedPosition);
            mAdapter.notifyDataSetChanged();
        }
        else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        if (mAdapter.mCrimes.size() > 0) {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
        else {
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        updateSubtitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        if ((getActivity().findViewById(R.id.detail_fragment_container) != null) && (mAdapter.mCrimes.size() > 0)) {
            mCrimeRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCrimeRecyclerView.findViewHolderForAdapterPosition(0).itemView.performClick();
                }
            }, 50);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                startNewCrime();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startNewCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
//        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
//        intent.putExtra(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
//        startActivityForResult(intent, START_CRIME_DETAILS);
        mCallbacks.onCrimeSelected(crime);
        mLastClickedPosition = RecyclerView.NO_POSITION;
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == START_CRIME_DETAILS) {
            boolean isRemoving = data.getBooleanExtra(CrimeFragment.EXTRA_IS_REMOVING, false);
            if (isRemoving) {
                mLastClickedPosition = RecyclerView.NO_POSITION;
            }
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }

    }

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        protected TextView mTitleTextView;
        protected TextView mDateTextView;
        protected CheckBox mSolvedCheckBox;
        protected Crime mCrime;

//        public CrimeHolder(View itemView) {
//            super(itemView);
//            this.itemView.setOnClickListener(this);
//            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
//            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
//            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
//            mSolvedCheckBox.setOnCheckedChangeListener(this);
//        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int resId) {
            super(inflater.inflate(resId, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mSolvedCheckBox.setOnCheckedChangeListener(this);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v) {
            mLastClickedPosition = getAdapterPosition();
//            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
//            intent.putExtra(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
//            startActivityForResult(intent, START_CRIME_DETAILS);
            mCallbacks.onCrimeSelected(mCrime);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mLastClickedPosition = getAdapterPosition();
            mCrime.setSolved(isChecked);
            CrimeLab.get(getActivity()).updateCrime(mCrime);
            if (getActivity().findViewById(R.id.detail_fragment_container) != null) {
                mCallbacks.onCrimeSelected(mCrime);
            }
        }

    }

    private class CrimePoliceHolder extends CrimeHolder {
        public CrimePoliceHolder(LayoutInflater inflater, ViewGroup parent, int resId) {
            super(inflater, parent, resId);
        }
    }

    class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int CRIME_VIEW_TYPE = 1;
        private final int CRIME_POLICE_VIEW_TYPE = 2;

        public List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemViewType(int position) {
            Log.d("CrimeListFragment", "CrimeAdapter " + mCrimes.get(position).isRequiresPolice());
            return !mCrimes.get(position).isRequiresPolice() ? CRIME_VIEW_TYPE : CRIME_POLICE_VIEW_TYPE;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            switch (viewType) {
                case CRIME_VIEW_TYPE:
                    return new CrimeHolder(layoutInflater, parent, R.layout.list_item_crime);
                case CRIME_POLICE_VIEW_TYPE:
                    return new CrimePoliceHolder(layoutInflater, parent, R.layout.list_item_crime_police);
                default:
                    return new CrimeHolder(layoutInflater, parent, R.layout.list_item_crime);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            ((CrimeHolder) holder).bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

    }

}
