package com.ambitious.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by lianhuibin on 2016/7/17.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
