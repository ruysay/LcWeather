package com.lc.weather.network
import com.lc.weather.models.WeatherBitForecastData
import com.lc.weather.ui.WeatherRepository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherBitRetrofitService {

    @GET("forecast/daily?key=${WeatherRepository.WEATHER_BIT_API_KEY}")
    fun getForecastWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<WeatherBitForecastData>
}