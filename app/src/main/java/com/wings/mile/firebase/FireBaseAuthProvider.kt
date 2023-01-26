package com.wings.mile.firebase

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wings.mile.Utils.Pref_storage
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FireBaseAuthProvider @Inject constructor(val firebaseAuth: FirebaseAuth) {

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var verificationId: String

    private lateinit var phoneCallbacksListener:PhoneCallbacksListener

    fun setPhoneCallbacksListener(listner: PhoneCallbacksListener) {
        this.phoneCallbacksListener = listner
    }

    init {
        firebaseAuth.setLanguageCode(Locale.getDefault().language)
    }

    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    phoneCallbacksListener.onVerificationCodeDetected(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        Log.e("cheking","invalid")
                        // Invalid request
                        phoneCallbacksListener.onVerificationFailed(e.message?:" ")
                    }
                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        phoneCallbacksListener.onVerificationFailed(e.message?:" ")
                    }
                    else -> {
                        Log.e("cheking1","invalid"+e.message)
                        phoneCallbacksListener.onVerificationFailed(e.message?:" ")
                    }
                }
                Log.e("cheking2","invalid"+e.message)
                Log.d("","FireBaseAuthProvider.onVerificationFailed e() ${e.message}")
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                resendToken = forceResendingToken
                phoneCallbacksListener.onCodeSent(s, forceResendingToken)
            }
        }

    fun sendVerificationCode(phone: String, activity: FragmentActivity?) {
        Pref_storage.setDetail(activity,"phone",phone.substring(3))
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone.trim(),
            60,
            TimeUnit.SECONDS,
            activity!!,
            callbacks
        )
    }

    fun verifyVerificationCode(code: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, code)
    }

    fun resendCode(phone: String, requireActivity: FragmentActivity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, requireActivity, callbacks, resendToken)
    }

    fun isUserVerified(): Boolean {
        return firebaseAuth.currentUser != null
    }
}

interface PhoneCallbacksListener {
    fun onVerificationCompleted()
    fun onVerificationCodeDetected(code: String)
    fun onVerificationFailed(message: String)
    fun onCodeSent(
        verificationId: String?,
        token: PhoneAuthProvider.ForceResendingToken?
    )
}