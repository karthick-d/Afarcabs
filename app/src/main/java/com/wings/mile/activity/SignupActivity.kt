package com.wings.mile.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.example.awesomedialog.*
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.databinding.SignupBinding
import com.wings.mile.model.SignUpRequest
import com.wings.mile.service.RetrofitService
import com.wings.mile.socialintegration.ProfileActivity
import com.wings.mile.socialintegration.Sigin
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.AuthSignUpBinding
import com.wings.mile.service.RetrofitService.Companion.gson
import java.util.regex.Pattern

class SignupActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {
    var signInButton: SignInButton? = null
    private var googleApiClient: GoogleApiClient? = null
    var textView: TextView? = null
    private val RC_SIGN_IN = 1
    private lateinit var binding: AuthSignUpBinding
    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()
    var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        binding = AuthSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        //binding.toolbarLayout.toolbarTitle.text = "Sign Up"

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        binding.signup.setOnClickListener {

            val number = binding.numberEdittext.text.toString()
            val email = binding.emailEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()
            val repassword = binding.retypeEdittext.text.toString()
            val lname = binding.accountLName.text.toString()
            val fname = binding.accountFName.text.toString()
            val token=Pref_storage.getDetail(this,"fcmtoken")

            val signup =
                SignUpRequest(email, fname, lname, number, password, 0, user_type_flg = "A", en_flg = "s", notification_token = token)

            Log.e("signup", "" +  gson.toJson(signup.toString()));
            val message = validation(number, email, password, repassword);

            if (message.isNullOrBlank()) {
                binding!!.LoginAvi.show()
                binding!!.LoginAvi.visibility= View.VISIBLE
                viewModel.sendSignupRequestOrProfileUpdate(
                    signup
                ).observe(this, Observer {
                    if (it != null) {
                        binding!!.LoginAvi.hide()
                        AwesomeDialog.build(this)
                            .title("Congratulations", null, R.color.labelrecyclerview)
                            .body("Account Created Successfully", null, R.color.labelrecyclerview)
                            .icon(R.drawable.user)
                            .onPositive(
                                "Ok",
                                R.drawable.login_border_theme

                            ) {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }


                    } else {
                        binding!!.LoginAvi.hide()
                        Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    }


                })
            } else {
                binding!!.LoginAvi.hide()
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }


    }

    fun validation(number: String, email: String, password: String, repassword: String): String? {
         if ( binding.accountFName.text.isNullOrEmpty()) {
             binding.accountFName.setError("First Name should not be empty")
            return "First Name should not be empty"
        }else if ( binding.accountLName.text.isNullOrEmpty()) {
            binding.accountLName.setError("Last Name should not be empty")
            return "Last Name should not be empty"
        }else if (number.isNullOrEmpty()) {
            binding.numberEdittext.setError("Phone number should not be empty")
            return "Phone number should not be empty"
        } else if (number.length < 10) {
            binding.numberEdittext.setError("Phone number not valid must contains 10 Number")
            return "Phone number not valid must contains 10 Number"
        } else if (email.isNullOrEmpty()) {
            binding.emailEdittext.setError("Email-ID should not be empty")
            return "Email-ID should not be empty"
        } else if (!isValidEmailId(email)) {
            binding.emailEdittext.setError("Invalid Email Address")
            return "Invalid Email Address"
        } else
            if (password.isNullOrEmpty()) {
                return "Password number should not be empty"
                binding.passwordEdittext.setError("Password  should not be empty")

            } else if (password.length < 8) {
                binding.passwordEdittext.setError("Password must contains minimum 8 characters")
                return "Password must contains minimum 8 characters"
            } else if (repassword.isNullOrEmpty()) {
                binding.retypeEdittext.setError("Retype Password should not be empty")
                return "Retype Password should not be empty"

            } else if (!password.equals(repassword)) {
                return "Password & Retype Password Mismatched"
            } else {
                return null
            }


    }

    fun LottieProgressDialog.Hide() {
        this.cancel()
    }

    fun LottieProgressDialog.Show() {
        this.show()
    }


    fun Dialog(): LottieProgressDialog {
        return LottieProgressDialog(
            context = this,
            isCancel = true,
            dialogWidth = null,
            dialogHeight = null,
            animationViewWidth = null,
            animationViewHeight = null,
            fileName = LottieProgressDialog.SAMPLE_1,
            title = null,
            titleVisible = null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Dialog().cancel()
    }

    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
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
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}