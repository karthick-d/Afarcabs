package com.wings.mile.firebase

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides


@Module
class AppModule {

    @Provides
    @AppScoped
    internal fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}