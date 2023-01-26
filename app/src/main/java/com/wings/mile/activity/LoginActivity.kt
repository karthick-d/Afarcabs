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
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.Gson
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.AuthSignInBinding
import com.wings.mile.databinding.ContentMainBinding
import com.wings.mile.firebase.AuthActivity
import com.wings.mile.listItem
import com.wings.mile.listener.clickListener
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

class LoginActivity : AppCompatActivity(), clickListener {
    private val TAG = "MainActivity"
    private lateinit var binding: AuthSignInBinding

    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        binding = AuthSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.login.isEnabled = true
          setNewFcm()
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        binding.login.setOnClickListener {

            val number = binding.numberEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            val message = validation(number, password);
            binding!!.LoginAvi.visibility= View.VISIBLE

            if (message.isNullOrBlank()) {
                binding!!.LoginAvi.show()
                binding.login.isEnabled = false
                viewModel.sendLoginRequest(
                    binding.numberEdit.text.toString(),
                    binding.passwordEdit.text.toString()
                ).observe(this, Observer {
                    if (it != null) {
                        binding!!.LoginAvi.hide()
                        Pref_storage.setDetail(this,"LoginRes", Gson().toJson(it.data))


                        val bundle = Bundle()

                        bundle.putString("phone", binding.numberEdit.text.toString())
                        val intent = Intent(this, OtpActivity::class.java)
                        intent.putExtra("Arguments", bundle)
                        binding.login.isEnabled = true

                        startActivity(intent)

                    } else {
                        binding.login.isEnabled = true
                        binding!!.LoginAvi.hide()

                        Toast.makeText(
                            this,
                            "Entered Email-ID / Password is invalid",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                })
            } else {
                binding!!.LoginAvi.hide()
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }


        binding.signup.setOnClickListener {
            it.let {

                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)

            }


        }


        binding.forgetpassword.setOnClickListener {
            it.let {

                val intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)

            }


        }



    }

    fun validation(number: String, password: String): String? {
         if (binding.numberEdit.text.isNullOrEmpty()) {
             binding.numberEdit.setError("Email-ID should not be empty")
            return "Email-ID should not be empty"
        } else if (password.isNullOrEmpty()) {
            return "Password number should not be empty"
        } else if (password.length < 8) {
            binding.passwordEdit.setError("Password must contains minimum 8 characters")
            return "Password must contains minimum 8 characters"
        } else {
            return null
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


