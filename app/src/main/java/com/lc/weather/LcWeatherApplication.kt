package com.lc.weather

import android.app.Application
import timber.log.Timber

class LcWeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        when (BuildConfig.BUILD_TYPE) {
            "debug" -> {
                //init Timber for debug
                Timber.plant(Timber.DebugTree())
            }
            "release" -> {
                //do something for release build
            }
        }
    }
}