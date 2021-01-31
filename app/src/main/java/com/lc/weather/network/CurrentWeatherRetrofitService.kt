package com.lc.weather.network

import com.lc.weather.models.WeatherData
import com.lc.weather.ui.WeatherRepository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrentWeatherRetrofitService {
    @GET("weather?units=metric&appid=${WeatherRepository.OPEN_WEATHER_API_KEY}")
    fun getCurrentWeather(@Query("q") location: String?): Call<WeatherData>

    @GET("weather?units=metric&appid=${WeatherRepository.OPEN_WEATHER_API_KEY}")
    fun getCurrentWeather(@Query("lat") lat: Double?, @Query("lon") lon: Double?): Call<WeatherData>
}