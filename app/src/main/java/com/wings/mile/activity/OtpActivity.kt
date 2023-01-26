package com.wings.mile.activity

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.CustomToast
import com.wings.mile.databinding.FragmentOtpVerificationBinding
import com.wings.mile.firebase.AuthActivityViewModel
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

class OtpActivity : BaseActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var viewModels: AuthActivityViewModel
    private lateinit var binding: FragmentOtpVerificationBinding
    private val retrofitService = RetrofitService.getInstance()
    lateinit var  otp:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("Arguments")
        val phone = bundle?.getString("phone")
        binding.textViewSubtitleOtpAuth.setText(String.format(getString(R.string.otp_auth_subtitle),phone))

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        binding.LoginAvi.show()
        binding.LoginAvi.visibility=View.VISIBLE

        val spannable = SpannableStringBuilder("Didn't received OTP? Resend")
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primary_700)),
            spannable.indexOf("Resend"), spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textViewTicker.text = spannable
         otp(phone)
        binding.buttonVerifyOtp.setOnClickListener(){
            binding.LoginAvi.show()
            if(otp.equals(binding.etOtp.text.toString())){
                val intent = Intent(this, DashboardActivity::class.java)
                Toast.makeText(this,"OTP Verified Successfully",Toast.LENGTH_SHORT).show()
                binding.LoginAvi.hide()
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"Invalid Otp",Toast.LENGTH_SHORT).show()
                binding.LoginAvi.hide()
            }

        }
        binding.textViewTicker.setOnClickListener {
            binding.LoginAvi.visibility=View.VISIBLE
            binding.LoginAvi.show()
            otp(phone)
        }

        binding.textViewTicker.postDelayed(Runnable { binding.textViewTicker.setVisibility(View.VISIBLE) }, 60000)


    }
    fun otp(phone: String?) {
        viewModel.validate(
            phone!!
        ).observe(this, Observer {
            if (it != null) {

                //binding.etOtp.setText(it.toString())
                binding.LoginAvi.hide()
                otp=it.toString()
                CustomToast.successToast(this, "Otp Send to Register Mail-Id ,it will expired in 1 Minute")


            } else {
                Toast.makeText(this,"Otp Not Recieved",Toast.LENGTH_LONG).show()

            }
        })

    }

}