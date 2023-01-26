package com.wings.mile.activity

import android.R.attr.logo
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.wings.mile.MapsActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.PrefManager
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivitySplashScreenBinding
import com.wings.mile.databinding.AuthSignInBinding
import com.wings.mile.databinding.PhonePermissionBinding


class DriverPermissionActivity : BaseActivity() {
    var English: ToggleButton? = null
    var Hindi: ToggleButton? = null
    var rl: RelativeLayout? = null
    private val handler = Handler()
    lateinit var dataBinding: PhonePermissionBinding
    val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
    protected val REQUIRED_PERMISSION_CAll =
        arrayOf(REQUEST_PHONE_CALL)
    private var prefManager: PrefManager? = null
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefManager = PrefManager(this)
        //Toast.makeText(this,""+prefManager!!.isFirstTimeLaunch,Toast.LENGTH_SHORT).show()

//        if (!prefManager!!.isFirstTimeLaunch) {
//            launchHomeScreen()
//            finish()
//        }

        call()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataBinding = PhonePermissionBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        dataBinding.confirm.setOnClickListener(){
            call()
        }

    }

    fun call() {

        if (!allPermissionsGrantphone()) {
            launchHomeScreen()
        } else {

            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION_CAll, 120)
        }
    }


    private fun launchHomeScreen() {
        prefManager!!.isFirstTimeLaunch = false
        val i = Intent(this, DriverLoginActivity::class.java)
        startActivity(i)
        finish()
    }
     fun allPermissionsGrantphone(): Boolean {
        for (permission in REQUIRED_PERMISSION_CAll) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true

            }
        }
        return false
    }

}