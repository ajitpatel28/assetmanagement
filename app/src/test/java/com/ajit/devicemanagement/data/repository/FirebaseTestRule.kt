package com.ajit.devicemanagement.data.repository

import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp


class FirebaseAppInitializer {
    companion object {
        private var initialized = false

        @Synchronized
        fun initializeFirebaseApp() {
            if (!initialized) {
                FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
                initialized = true
            }
        }
    }
}
