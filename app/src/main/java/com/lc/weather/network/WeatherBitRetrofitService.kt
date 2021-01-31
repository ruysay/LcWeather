package com.lc.weather.network
import com.lc.weather.models.WeatherBitForecastData
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherBitRetrofitService {

    @GET("/forecast/daily")
    fun getForecastWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<WeatherBitForecastData>
}