package com.wings.mile.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.dialog.CustomSuccessDialogFragment
import com.wings.mile.dialog.OptionDialogFragment
import com.wings.mile.firebase.BaseApplication
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


abstract class BaseActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(App.getApplicationTheme());
        super.onCreate(savedInstanceState)
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(BaseApplication.localeManager.setLocale(base))
    }
    override fun onResume() {
        super.onResume()
        //Set Listener to receive events
       // App.registerSessionListener(this)

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        //reset session when user interact
        App.resetSession()
    }

//    override fun onSessionLogout() {
//       // Toast.makeText(this,"session logout",Toast.LENGTH_LONG).show()
//        Log.e("session","sessionlogout")
//        // Do You Task on session out
//        val intent = Intent(this, DriverLoginActivity::class.java)
//        intent.addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK
//        )
//        startActivity(intent)
//    }
    private fun setLanguageForApp(languageToLoad: String) {
        val locale: Locale
        locale = if (languageToLoad == "not-set") { //use any value for default
            Locale.getDefault()
        } else {
            Locale(languageToLoad)
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        getBaseContext().getResources().updateConfiguration(
            config,
            getBaseContext().getResources().getDisplayMetrics()
        )
    }






}