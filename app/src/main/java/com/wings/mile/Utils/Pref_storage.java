package com.wings.mile.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ansari on 2/2/17.
 */

public class Pref_storage {

    private static SharedPreferences sharedPreferences = null;


    public static void openPref(Context context) {
        try {

            sharedPreferences = context.getSharedPreferences("AFARCABS", Context.MODE_PRIVATE);

        } catch (Exception e) {
            Log.e("Error",e.toString());
        }

    }


    public static void deleteKey(Context context, String key) {
        HashMap<String, String> result = new HashMap<String, String>();

        Pref_storage.openPref(context);
        for (Map.Entry<String, ?> entry : Pref_storage.sharedPreferences.getAll().entrySet()) {
            result.put(entry.getKey(), (String) entry.getValue());
        }

        boolean b = result.containsKey(key);
        if (b) {
            Pref_storage.openPref(context);
            SharedPreferences.Editor prefsPrivateEditor = Pref_storage.sharedPreferences.edit();
            prefsPrivateEditor.remove(key);
            prefsPrivateEditor.commit();
            Pref_storage.sharedPreferences = null;
        }
    }

    public static void setDetail(Context context, String key, String value) {
        try {
            Pref_storage.openPref(context);
            SharedPreferences.Editor prefsPrivateEditor = Pref_storage.sharedPreferences.edit();
            prefsPrivateEditor.putString(key, value);
            prefsPrivateEditor.commit();
            Pref_storage.sharedPreferences = null;
        } catch (Exception e) {
            Log.e("Error",e.toString());
        }

    }


    public static String getDetail(Context context, String key) {

        HashMap<String, String> result = new HashMap<String, String>();

        Pref_storage.openPref(context);

        for (Map.Entry<String, ?> entry : Pref_storage.sharedPreferences.getAll().entrySet()) {

            result.put(entry.getKey(), (String) entry.getValue());

        }

        String b = result.get(key);

        return b;

    }
}

