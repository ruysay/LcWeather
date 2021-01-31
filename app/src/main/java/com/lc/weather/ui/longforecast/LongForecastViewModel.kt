package com.lc.weather.ui.longforecast

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.lc.weather.enums.LoadStates
import com.lc.weather.models.WeatherUiModel
import com.lc.weather.ui.WeatherRepository

class LongForecastViewModel : ViewModel() {
    private val repository = WeatherRepository

    fun init(context: Context) {
        repository.init(context)
    }

    fun getLoadState(): MutableLiveData<LoadStates> {
        return repository.getLoadState()
    }

    fun getLongDurationForecast(
        city: String,
        latLng: LatLng
    ): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        return repository.getLongDurationForecast(
            city,
            latLng
        )
    }
}