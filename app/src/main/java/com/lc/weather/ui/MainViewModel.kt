package com.lc.weather.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.lc.weather.models.WeatherUiModel

class MainViewModel : ViewModel() {
    private val repository = WeatherRepository

    fun init(context: Context) {
        repository.init()
    }

    fun getWeather(query: String): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        return repository.getWeather(query)
    }

    fun getWeatherList(): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        return repository.getWeatherList()
    }

    fun getWeather(latLng: LatLng): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        return repository.getWeather(latLng)
    }

//    fun getCurrentWeather(query: String): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
//        return repository.getCurrentWeather(query)
//    }
//
//    fun getForecastWeather(): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
//        return repository.getForecastWeather()
//    }
}