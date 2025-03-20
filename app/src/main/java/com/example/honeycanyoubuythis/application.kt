package com.example.honeycanyoubuythis

import android.app.Application
import com.google.firebase.FirebaseApp

class HoneyCanYouBuyThis : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}