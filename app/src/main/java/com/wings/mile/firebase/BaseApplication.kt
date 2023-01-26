package com.wings.mile.firebase

import android.content.Context
import androidx.databinding.ktx.BuildConfig
import com.wings.mile.activity.LocaleManager
import com.wings.mile.firebase.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class BaseApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
    companion object {
        lateinit var localeManager: LocaleManager
        var gpsPermission: Boolean = false
        var forground: Boolean = false
        lateinit var instance: BaseApplication

        fun getContext(): Context {
            return instance.applicationContext
        }

    }
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            //Timber.plant(Timber.DebugTree())
        }
    }
    override fun attachBaseContext(base: Context?): Unit {
        localeManager = LocaleManager(base)
        super.attachBaseContext(localeManager.setLocale(base))
    }
}