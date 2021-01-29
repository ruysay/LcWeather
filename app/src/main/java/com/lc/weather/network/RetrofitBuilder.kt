package com.lc.carview.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val carRetrofitService by lazy {
    RetrofitBuilder.createCarRetrofitService()
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
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build()
        }

        fun createCarRetrofitService(): CarsRetrofitService {
            val baseUrl = "https://afterpay-mobile-interview.s3.amazonaws.com/"
            //create retrofit service with 60 sec timeout
            return createCloudBuild(baseUrl, 60).create(CarsRetrofitService::class.java)
        }
    }
}