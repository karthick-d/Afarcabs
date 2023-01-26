package com.wings.mile.firebase

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [AuthActivityModule::class, FragmentBindingModule::class])
    internal abstract fun bindAuthActivity(): AuthActivity



}