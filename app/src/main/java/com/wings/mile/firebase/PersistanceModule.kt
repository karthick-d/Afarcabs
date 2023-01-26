package com.wings.mile.firebase

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class PersistanceModule {

    @Provides
    @AppScoped
    internal fun provideSharedPreference(context: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}