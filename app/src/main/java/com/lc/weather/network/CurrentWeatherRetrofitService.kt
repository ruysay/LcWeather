package com.lc.weather.network

import com.lc.weather.R
import com.lc.weather.models.WeatherData
import com.lc.weather.ui.WeatherRepository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrentWeatherRetrofitService {
    @GET("weather?units=metric&appid=${WeatherRepository.API_KEY}")
    fun getCurrentWeather(@Query("q") location: String?): Call<WeatherData>

    @GET("weather?units=metric&appid=${WeatherRepository.API_KEY}")
    fun getCurrentWeather(@Query("lat") lat: Double?, @Query("lon") lon: Double?): Call<WeatherData>


//    /**
//     * Below are APIs not in use in normal flow
//     * Enable them when running unit test cases
//     */
//    @GET("cars_empty.json")
//    fun getEmptyCars(): Call<List<CarData>>
//
//    @GET("cars_error.json")
//    fun getInvalidCars(): Call<List<CarData>>
}