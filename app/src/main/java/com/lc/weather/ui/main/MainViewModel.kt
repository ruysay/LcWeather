package com.lc.weather.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.lc.weather.enums.LoadStates
import com.lc.weather.models.WeatherUiModel
import com.lc.weather.ui.WeatherRepository

class MainViewModel : ViewModel() {
    private val repository = WeatherRepository

    fun init(context: Context) {
        repository.init(context)
    }

    fun getWeather(query: String): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        return repository.getWeather(query)
    }

    fun getWeatherList(): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        return repository.getWeatherList()
    }

    fun getLoadState(): MutableLiveData<LoadStates> {
        return repository.getLoadState()
    }
}