package com.wings.mile.firebase

import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.awesomedialog.*
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.NavigationActivity
import com.wings.mile.Payment.PaymentAccountActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivityAuthBinding
import com.wings.mile.model.SignUpRequest
import com.wings.mile.model.getdriver
import com.wings.mile.service.RetrofitService.Companion.retrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.util.*
import javax.inject.Inject

class AuthActivity : BaseActivity<ActivityAuthBinding, AuthActivityViewModel>(),
    AuthActivityViewInteractor {

    companion object {
        private const val CREDENTIAL_PICKER_REQUEST = 1
        private const val SMS_CONSENT_REQUEST = 2

        fun getIntent(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }


    override fun getViewModelClass(): Class<AuthActivityViewModel> =
        AuthActivityViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_auth

    @Inject
    lateinit var authPagerAdapter: AuthPagerAdapter
    lateinit var viewModels: MainViewModel
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var savedrivers: getdriver
    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            //startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
                        } catch (e: ActivityNotFoundException) {
                            showSnackBar(e.message?: "Something went wrong")
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.viewInteractor = this

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)

        setUpPager()

        if (savedInstanceState == null) {
            requestHint()
        }
        if (Pref_storage.getDetail(this, "language") == "English") {

            setLanguageForApp("en")
        } else {

            setLanguageForApp("ta")
        }
        viewModel.pagerPagePosition.observe(
            this, Observer<Int?> { value ->
                binding.authViewPager.currentItem = value ?: 0
            }
        )
    }

    private fun setUpPager() {
        authPagerAdapter.setCount(2)
        binding.authViewPager.adapter = authPagerAdapter
        binding.authViewPager.isUserInputEnabled = false
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient = Credentials.getClient(this)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            CREDENTIAL_PICKER_REQUEST,
            null, 0, 0, 0
        )
    }

    override fun startSMSListener() {
        val smsRetrieverClient = SmsRetriever.getClient(this)
        val task = smsRetrieverClient.startSmsUserConsent(null)

        task.addOnSuccessListener {
           // Toast.makeText(this, getString(R.string.sms_start), Toast.LENGTH_LONG).show()
        }
        task.addOnFailureListener {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    viewModel.selectedPhoneNumber.value = credential?.id
                }

            SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val oneTimeCode = message?.substring(0, 6)
                    Log.d("","AuthActivity.onActivityResult message $oneTimeCode")
                    viewModel.selectedOtpNumber.value = oneTimeCode?.trim()
                }
        }
    }

    override fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) {
                if (it.isSuccessful) {
                    goToGoalActivity()
                } else {
                    // Show Error
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        showSnackBar(it.exception?.message?: "Verification Failed")
                    } else {
                        showSnackBar("Verification Failed")
                    }
                }
            }
    }

    override fun onBackPressed() {
        when (binding.authViewPager.currentItem) {
            AuthActivityViewModel.PHONE_VERIFICATION_PAGE -> super.onBackPressed()
            AuthActivityViewModel.OTP_VERIFICATION_PAGE -> {
                binding.authViewPager.currentItem = AuthActivityViewModel.PHONE_VERIFICATION_PAGE
                viewModel.clearCountdownTick()
            }
            else -> super.onBackPressed()
        }
    }

    override fun goToGoalActivity() {

        if (Pref_storage.getDetail(this, "login").equals("login")) {
            val request = Pref_storage.getDetail(this,"getdriverdetails")
            savedrivers = Gson().fromJson(request.toString(), getdriver::class.java)
            if(savedrivers.regstr_pymt_flg.equals("S")){
                val intent = Intent(this, NavigationActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                val intent = Intent(this, PaymentAccountActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            viewModels =
                ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService!!))).get(
                    MainViewModel::class.java
                )
            val token = Pref_storage.getDetail(this, "fcmtoken")
//        val intent = Intent(this, DashboardActivity::class.java)
//        startActivity(intent)
//        finish()
            val value = Pref_storage.getDetail(this, "phone")
            val signup =
                SignUpRequest(
                    "", "", "", value, "", 0, user_type_flg = "D", en_flg = "s",
                    notification_token = token
                )
            Log.e("signup", "" + signup)

            viewModels.sendSignupRequestOrProfileUpdate(signup).observe(this) {
                it.let { resource ->
                    when (resource.status) {
                        com.wings.mile.Utils.Status.LOADING -> {
                        }
                        com.wings.mile.Utils.Status.SUCCESS -> {
                            AwesomeDialog.build(this)
                                .title("Congratulations", null, R.color.labelrecyclerview)
                                .body(
                                    "Account Created Successfully",
                                    null,
                                    R.color.labelrecyclerview
                                )
                                .icon(R.drawable.user)
                                .onPositive(
                                    "Ok",
                                    R.drawable.login_border_theme

                                ) {
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                        com.wings.mile.Utils.Status.ERROR -> {
                            // Toast.makeText(this,it.message.toString(),Toast.LENGTH_LONG).show()
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }



    override fun showSnackBarMessage(message: String) {
        showSnackBar(message)
    }

    override fun onDestroy() {
        unregisterReceiver(smsVerificationReceiver)
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (currentFocus != null){

            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
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