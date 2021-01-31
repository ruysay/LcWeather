package com.lc.weather.network

import com.lc.weather.models.ForecastWeatherData
import com.lc.weather.ui.WeatherRepository
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastWeatherRetrofitService {
    @GET("onecall?exclude=hourly,minutely&units=metric&appid=${WeatherRepository.OPEN_WEATHER_API_KEY}")
    fun getForecastWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<ForecastWeatherData>

    @GET("onecall?exclude=hourly,minutely&units=metric&appid=${WeatherRepository.OPEN_WEATHER_API_KEY}")
    fun getRxForecastWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Observable<ForecastWeatherData>
}