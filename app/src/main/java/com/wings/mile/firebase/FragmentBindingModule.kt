package com.wings.mile.firebase


import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBindingModule {
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun bindOtpVerificationFragment(): OtpVerificationFragment

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun bindPhoneVerificationFragment(): PhoneVerificationFragment

}