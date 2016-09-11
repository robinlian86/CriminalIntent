package com.ambitious.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by lianhuibin on 2016/7/16.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private CriminalIntentJSONSerializer mSerializer;
    private ArrayList<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private CrimeLab(Context appContext){
        mAppContext = appContext;
//        mCrimes = new ArrayList<Crime>();
        mSerializer = new CriminalIntentJSONSerializer(FILENAME, mAppContext);

        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading crimes: ", e);
        }

//        for (int i = 0; i < 100; i++) {
//            Crime c = new Crime();
//            c.setTitle("Crime #" + i);
//            c.setSolved(i % 2 == 0);
//            mCrimes.add(c);
//        }
    }

    public static CrimeLab get(Context c){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }

        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
        Log.d(TAG, "addCrime size is " + mCrimes.size());
    }

    public void deleteCrime(Crime c) {
        mCrimes.remove(c);
        saveCrimes();
        Log.d(TAG, "deleteCrime size is " + mCrimes.size());
    }

    public ArrayList<Crime> getCrimes() {
        Log.d(TAG, "getCrimes size is " + mCrimes.size());
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file, crime size is " + mCrimes.size());
            return true;
        } catch (Exception e) {
            Log.d(TAG, "error saving crimes: ", e);
            return false;
        }
    }
}
