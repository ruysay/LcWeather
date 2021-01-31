package com.lc.weather.network

import com.lc.weather.ui.WeatherRepository
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class WeatherBitServiceInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //Ignore x-api-key or x-auto-token since they are not required in API calls
        //Add header such as above in this Interceptor if we need to.
        val originalRequest = chain.request()
        val newRequestBuilder = originalRequest.newBuilder()
        newRequestBuilder.addHeader("X-Rapidapi-Key", WeatherRepository.WEATHER_BIT_API_KEY)
        newRequestBuilder.addHeader("X-Rapidapi-Host", WeatherRepository.WEATHER_BIT_HOST)
        return chain.proceed(newRequestBuilder.build())
    }
}