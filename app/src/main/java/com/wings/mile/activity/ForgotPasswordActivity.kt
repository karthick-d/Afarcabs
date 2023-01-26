package com.wings.mile.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.example.awesomedialog.*
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ForgerpasswordBinding
import com.wings.mile.model.SignUpRequest
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

import java.util.regex.Pattern

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ForgerpasswordBinding
    lateinit var viewModel: MainViewModel
    private val retrofitService = RetrofitService.getInstance()
    var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgerpassword)
        binding = ForgerpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.toolbarTitle.text = "Forgot Password"

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        binding.submit.setOnClickListener {

            val number = binding.mobileEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()
            val repassword = binding.retypeEdittext.text.toString()
            val token= Pref_storage.getDetail(this,"fcmtoken")

            val signup = SignUpRequest("", "", "", number, password, 101, user_type_flg = "", en_flg = "",
            notification_token = token)
            Log.e("signup",""+signup.toString());

            val message = validation(number, password, repassword);


            if (message.isNullOrBlank()) {
                binding!!.LoginAvi.show()
                viewModel.sendSignupRequestOrProfileUpdate(
                    signup
                ).observe(this, Observer {
                    if (it!=null) {
                        binding!!.LoginAvi.hide()
                        AwesomeDialog.build(this)
                            .title("Congratulations", null, R.color.labelrecyclerview)
                            .body("Update Password Successfully", null, R.color.labelrecyclerview)
                            .icon(R.drawable.user)
                            .onPositive(
                                "Ok",
                                R.drawable.login_border_theme

                            ) {
                                val intent= Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }


                    } else {
                        binding!!.LoginAvi.hide()
                        Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show()
                    }


                })
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }


    }

    fun validation(number: String, password: String, repassword: String): String? {
        if (number.isNullOrEmpty()) {
            binding.mobileEdittext.setError("Phone number should not be empty")
            return "Phone number should not be empty"
        } else if (number.length < 10) {
            binding.mobileEdittext.setError("Phone number not valid must contains 10 Number")
            return "Phone number not valid must contains 10 Number"
        } else if (password.isNullOrEmpty()) {
            binding.passwordEdittext.setError("Password number should not be emptyr")

            return "Password number should not be empty"
        } else if (password.length < 8) {
            binding.passwordEdittext.setError("Password must contains minimum 8 characters")
            return "Password must contains minimum 8 characters"
        }else if (repassword.isNullOrEmpty()) {
            binding.retypeEdittext.setError("Retype Password should not be empty")

            return "Retype Password should not be empty"
        } else if (!password.equals(repassword)) {
            binding.retypeEdittext.setError("New Password & Confirm Password Missmatched")

            return "New & Confirm Password Missmatched"
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
}