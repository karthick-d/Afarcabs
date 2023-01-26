package com.wings.mile.firebase


import dagger.Module
import dagger.Provides


@Module
class AuthActivityModule {
    @Provides
    @ActivityScoped
    fun provideResourceProvider(context: AuthActivity): BaseResourceProvider {
        return ResourceProvider(context)
    }

    @Provides
    @ActivityScoped
    fun provideAuthPagerAdapter(activity: AuthActivity): AuthPagerAdapter {
        return AuthPagerAdapter(activity)
    }
}