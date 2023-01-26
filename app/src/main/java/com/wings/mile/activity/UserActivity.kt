package com.wings.mile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.google.gson.Gson
import com.wings.mile.R
import com.wings.mile.databinding.ContentMainBinding
import com.wings.mile.databinding.UserdefinBinding
import com.wings.mile.firebase.AuthActivity
import com.wings.mile.listItem
import com.wings.mile.listener.clickListener
import com.wings.mile.service.RetrofitService
import com.wings.mile.socialintegration.ProfileActivity
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

class UserActivity : AppCompatActivity(), clickListener,GoogleApiClient.OnConnectionFailedListener {
    private val TAG = "MainActivity"
    private lateinit var binding: UserdefinBinding
    private val RC_SIGN_IN = 1
    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()
    private var googleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userdefin)

        binding = UserdefinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )



        binding.phone.setOnClickListener {
            it.let {

                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)

            }


        }


        binding.signInButton.setOnClickListener {
            it.let {
                val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient!!)
                startActivityForResult(intent, RC_SIGN_IN)

            }


        }


    }



    override fun onItem(res: listItem) {

        val bundle = bundleOf(
            "firstName" to res.firstName,
            "lastName" to res.lastName,
            "jobtitle" to res.jobtitle,
            "email" to res.email,

            )
//        val intent: Intent = Intent(this, DetailActivity::class.java)
//        intent.putExtra("Res", bundle)
//        startActivity(intent)
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult?) {
        if (result!!.isSuccess) {
            gotoProfile()
        } else {
            Toast.makeText(applicationContext, "Sign in cancel", Toast.LENGTH_LONG).show()
        }
    }
    private fun gotoProfile() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}


