package com.mindbyromanzanoni

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = this
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    companion object {
        lateinit var mContext: ApplicationClass
        lateinit var instance: ApplicationClass
        fun get(): ApplicationClass = instance
    }
}