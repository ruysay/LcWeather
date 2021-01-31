package com.lc.weather.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class WeatherBitServiceInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val newRequestBuilder = originalRequest.newBuilder()
        return chain.proceed(newRequestBuilder.build())
    }
}