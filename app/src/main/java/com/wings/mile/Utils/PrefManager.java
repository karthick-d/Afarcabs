package com.wings.mile.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "Afarcabs";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    final SharedPreferences pref;
    final SharedPreferences.Editor editor;
    final Context _context;
    // shared pref mode
    final int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

}
