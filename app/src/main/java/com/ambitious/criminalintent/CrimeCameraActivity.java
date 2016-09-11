package com.ambitious.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

/**
 * Created by lianhuibin on 2016/8/31.
 */
public class CrimeCameraActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }
}
