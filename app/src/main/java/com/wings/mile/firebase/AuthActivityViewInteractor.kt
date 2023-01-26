package com.wings.mile.firebase

import com.google.firebase.auth.PhoneAuthCredential

interface AuthActivityViewInteractor: ViewInteractor {

    fun showSnackBarMessage(message: String)

    fun goToGoalActivity()

    fun startSMSListener()

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential)
}