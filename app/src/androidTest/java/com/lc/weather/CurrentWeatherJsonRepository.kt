package com.lc.weather

import com.lc.weather.models.WeatherData
import com.lc.weather.network.CurrentWeatherRetrofitService
import retrofit2.Call

/**
 * A proxy to test API which is served by MockServer
 */
class CurrentWeatherJsonRepository(private val api: CurrentWeatherRetrofitService) {
    fun getCurrentWeather(): Call<WeatherData> = api.getCurrentWeather("")

}