package com.lc.weather.network

import com.lc.weather.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val currentWeatherRetrofitService by lazy {
    RetrofitBuilder.createCurrentRetrofitService()
}

val forecastRetrofitService by lazy {
    RetrofitBuilder.createForecastRetrofitService()
}

/*

 */
class RetrofitBuilder {
    companion object {
        private val serviceInterceptor = ServiceInterceptor()
        private fun createCloudBuild(baseUrl: String, timeout: Long = 0): Retrofit {
            val okHttpClient =
                OkHttpClient().newBuilder().addInterceptor(serviceInterceptor)
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS)

            // Set timeout before building it
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build()
        }

        fun createCurrentRetrofitService(): CurrentWeatherRetrofitService {
            val baseUrl = "https://api.openweathermap.org/data/2.5/"
            //create retrofit service with 60 sec timeout
            return createCloudBuild(baseUrl, 60).create(CurrentWeatherRetrofitService::class.java)
        }

        fun createForecastRetrofitService(): ForecastWeatherRetrofitService {
            val baseUrl = "https://api.openweathermap.org/data/2.5/"
            //create retrofit service with 60 sec timeout
            return createCloudBuild(baseUrl, 60).create(ForecastWeatherRetrofitService::class.java)
        }
    }
}