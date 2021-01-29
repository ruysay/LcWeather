package com.lc.weather

import android.app.Application
import android.content.Context
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File

class LcWeatherApplication : Application() {

    companion object {
        lateinit var application: Application
        var picassoWithCache: Picasso? = null

        //an universal context across the app
        fun getContext(): Context {
            return application.applicationContext
        }

    }
    override fun onCreate() {
        super.onCreate()
        application = this
        initPicasso()

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

    private fun initPicasso() {
        val httpCacheDirectory = File(cacheDir, "picasso-cache")
        val cache = Cache(httpCacheDirectory, 15 * 1024 * 1024)

        val okHttpClientBuilder =
            OkHttpClient.Builder().cache(cache)

        picassoWithCache =
            Picasso.Builder(this).downloader(OkHttp3Downloader(okHttpClientBuilder.build())).build()

    }
}