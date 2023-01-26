package com.wings.mile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.Gson
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ContentMainBinding
import com.wings.mile.databinding.DriverPhoneAuthBinding
import com.wings.mile.databinding.UserdefinBinding
import com.wings.mile.firebase.AuthActivity
import com.wings.mile.listItem
import com.wings.mile.listener.clickListener
import com.wings.mile.service.RetrofitService
import com.wings.mile.socialintegration.ProfileActivity
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

class DriverRegisterActivity : BaseActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: DriverPhoneAuthBinding
    private val RC_SIGN_IN = 1
    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()
    private var googleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_phone_auth)

        binding = DriverPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNewFcm()

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )



        binding.phone.setOnClickListener {
            it.let {

                val intent = Intent(this, AuthActivity::class.java)
                Pref_storage.setDetail(this,"login", "register")
                startActivity(intent)

            }


        }





    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setNewFcm() {
        FirebaseInstallations.getInstance().getToken(true)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                if (task.result != null) {
                    val token: String = task.result.token
                    Pref_storage.setDetail(this,"fcmtoken",token)
                    Log.e("token-->",""+token);
                }
            }
    }

}


