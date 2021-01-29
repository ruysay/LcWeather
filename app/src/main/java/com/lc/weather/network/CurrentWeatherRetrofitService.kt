package com.lc.carview.network

import com.lc.carview.models.CarData
import com.lc.weather.models.ForecastWeatherData
import com.lc.weather.models.WeatherData
import retrofit2.Call
import retrofit2.http.GET

interface WeatherRetrofitService {
    @GET("cars.json")
    fun getCurrentWeather(): Call<WeatherData>


    @GET("cars.json")
    fun getCurrentWeather(): Call<ForecastWeatherData>

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