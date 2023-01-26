package com.wings.mile.activity;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.N;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;


import com.wings.mile.Utils.Pref_storage;
import com.wings.mile.Utils.Utility;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class LocaleManager {

      SharedPreferences prefs;

    public LocaleManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public Context setLocale(Context context) {
        return updateResources(context, getLanguage(context));
    }



    public String getLanguage(Context context) {
        if (Pref_storage.getDetail(context, "language") != null) {
            if (Pref_storage.getDetail(context, "language").equals("English"))
            {
                return "en";
            }
            else
            {
                return "ta";
            }
        }

        return "en";
    }



    private Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Utility.isAtLeastVersion(N)) {
            setLocaleForApi24(config, locale);
            context = context.createConfigurationContext(config);
        } else if (Utility.isAtLeastVersion(JELLY_BEAN_MR1)) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    @RequiresApi(api = N)
    private void setLocaleForApi24(Configuration config, Locale target) {
        Set<Locale> set = new LinkedHashSet<>();
        // bring the target locale to the front of the list
        set.add(target);

        LocaleList all = LocaleList.getDefault();
        for (int i = 0; i < all.size(); i++) {
            // append other locales supported by the user
            set.add(all.get(i));
        }

        Locale[] locales = set.toArray(new Locale[0]);
        config.setLocales(new LocaleList(locales));
    }
}