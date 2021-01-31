package com.lc.weather.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ServiceInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //Ignore x-api-key or x-auto-token since they are not required in API calls
        //Add header such as above in this Interceptor if we need to.
        val originalRequest = chain.request()
        val newRequestBuilder = originalRequest.newBuilder()

        return chain.proceed(newRequestBuilder.build())
    }
}