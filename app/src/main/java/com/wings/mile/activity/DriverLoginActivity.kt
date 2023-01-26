package com.wings.mile.activity

import android.R.attr.logo
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.MapsActivity
import com.wings.mile.NavigationActivity
import com.wings.mile.Payment.PaymentAccountActivity
import com.wings.mile.Payment.PaymentRazorActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivitySplashScreenBinding
import com.wings.mile.databinding.AuthSignInBinding
import com.wings.mile.databinding.DriverLoginBinding
import com.wings.mile.databinding.PhonePermissionBinding
import com.wings.mile.firebase.AuthActivity
import com.wings.mile.location.MainActivity
import com.wings.mile.location.MapActivity
import com.wings.mile.model.getdriver
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory


class DriverLoginActivity : BaseActivity() {
    var English: ToggleButton? = null
    var Hindi: ToggleButton? = null
    var rl: RelativeLayout? = null
    private val handler = Handler()
    lateinit var dataBinding: DriverLoginBinding

    //    val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
//    protected val REQUIRED_PERMISSION_CAll =
//        arrayOf(REQUEST_PHONE_CALL)
    lateinit var viewModel: MainViewModel
    lateinit var savedrivers: getdriver
    private val retrofitService = RetrofitService.getInstance()
    var flag: Boolean = false

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataBinding = DriverLoginBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        try {
            val value = Pref_storage.getDetail(this, "phone")
            if (value == null) {

            } else {
                apicall()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dataBinding.confirm.setOnClickListener() {
            val value = Pref_storage.getDetail(this, "phone")
            if (!flag) {
                if (value == null) {
                    val intent = Intent(this, DriverRegisterActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, getString(R.string.complte_register), Toast.LENGTH_LONG).show()

            }
        }
        dataBinding.login.setOnClickListener() {
            val intent = Intent(this, AuthActivity::class.java)
            Pref_storage.setDetail(this, "login", "login")
            startActivity(intent)


        }

    }

    fun apicall() {
        viewModel!!.getdriverdetails(Pref_storage.getDetail(this, "phone")).observe(this) {
            it.let { resource ->
                when (resource.status) {
                    com.wings.mile.Utils.Status.LOADING -> {
                    }
                    com.wings.mile.Utils.Status.SUCCESS -> {

                        Pref_storage.setDetail(
                            this,
                            "getdriverdetails",
                            Gson().toJson(it.data!!.get(0))
                        )
                        val request = Pref_storage.getDetail(this, "getdriverdetails")
                        Pref_storage.setDetail(this, "userid", it.data?.get(0)?.user_id.toString())
                        savedrivers = Gson().fromJson(request.toString(), getdriver::class.java)
                        if (savedrivers.en_flag.equals("A")) {
                            flag = true
                        } else {
//                            val intent = Intent(this, DashboardActivity::class.java)
//                            startActivity(intent)
                        }

                    }
                    com.wings.mile.Utils.Status.ERROR -> {
                        if (it.data == null) {
                            flag = false
                        }

                        // binding!!.LoginAvi.hide()

                    }
                }
            }
        }
    }


}