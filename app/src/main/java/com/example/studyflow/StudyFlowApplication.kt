package com.example.studyflow

import android.app.Application
import com.google.firebase.FirebaseApp

class StudyFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
