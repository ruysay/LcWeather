package com.lc.weather.ui

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.lc.weather.enums.LoadStates
import com.lc.weather.models.Daily
import com.lc.weather.network.currentWeatherRetrofitService
import com.lc.weather.models.WeatherData
import com.lc.weather.models.WeatherUiModel
//import com.google.android.gms.maps.model.LatLng
import com.lc.weather.models.ForecastWeatherData
import com.lc.weather.network.forecastRetrofitService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

object WeatherRepository {
    private val cache = MutableLiveData<HashMap<String, MutableList<WeatherUiModel>>>()

    private val cachedWeather = HashMap<String, MutableList<WeatherUiModel>>()

    const val API_KEY = "4b9db7aefc049226b4a23f16724554eb" //R.string.open_weather_api_key

    private lateinit var disposable: Disposable

    private val loadState = MutableLiveData<LoadStates>(LoadStates.START)
    private var context: Context? = null

    private val activity: Activity? by lazy {
        context as? Activity
    }

    fun init(context: Context) {
        this.context = context
    }

    fun getWeather(latLng: LatLng): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        currentWeatherRetrofitService.getCurrentWeather(latLng.latitude, latLng.longitude)
            .enqueue(object : Callback<WeatherData> {
                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    Timber.d("getWeatherLatLng - onFailure")
                }

                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                    Timber.d("getWeatherLatLng - onResponse: ${response.body()}")

                    //TODO parse response and save to cache
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val isNewData = cachedWeather[latLng.toString()] == null
                            val query = latLng.toString()
                            if (isNewData) {
                                cachedWeather[query] = mutableListOf()
                                cachedWeather[query]?.add(0, transform(it))
                            } else {
                                cachedWeather[query]?.set(0, transform(it))
                            }

                            Timber.d("getWeatherLatLng step 1 -  ${cachedWeather.size}")
                            //get 7 day forecast with the latLng in response
                            disposable = forecastRetrofitService.getRxForecastWeather(
                                it.coord.lat,
                                it.coord.lon
                            )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result ->
                                    //TODO update cache here
                                    //set cache to notify observer data change
                                    Timber.d("getWeatherLatLng - $result")
                                    result.daily.mapIndexed { index, daily ->
                                        when (index) {
                                            0 -> {
                                            } // we already have data from get current weather API so ignore.
                                            else -> {
                                                if (isNewData) {
                                                    cachedWeather[query]?.add(
                                                        index,
                                                        transform(daily)
                                                    )
                                                } else {
                                                    cachedWeather[query]?.set(
                                                        index,
                                                        transform(daily)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Timber.d("getWeatherLatLng step 2 -  ${cachedWeather[query]?.size}")
                                    setWeatherData(cachedWeather)
                                    if (!disposable.isDisposed) {
                                        disposable.dispose()
                                    }
                                }, {
                                    Timber.e(it)
                                    if (!disposable.isDisposed) {
                                        disposable.dispose()
                                    }
                                })
                        }
                    }
                }
            })
        return cache
    }

    fun getWeatherList(): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        return cache
    }

    fun getWeather(query: String): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        currentWeatherRetrofitService.getCurrentWeather(query)
            .enqueue(object : Callback<WeatherData> {
                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    Timber.d("getWeather - onFailure")
                    setLoadState(LoadStates.ERROR)
                }

                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                    Timber.d("getWeather $query- onResponse: ${response.body()}")

                    //parse response and save to cache
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val isNewData = cachedWeather[query] == null

                            if (isNewData) {
                                setLoadState(LoadStates.LOADING)
                                cachedWeather[query] = mutableListOf()
                                cachedWeather[query]?.add(0, transform(it))
                            } else {
                                cachedWeather[query]?.set(0, transform(it))
                            }

                            Timber.d("getWeather step 1 -  ${cachedWeather.size}")
                            //get 7 day forecast with the latLng in response
                            disposable = forecastRetrofitService.getRxForecastWeather(
                                it.coord.lat,
                                it.coord.lon
                            )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result ->
                                    //set cache to notify observer data change
                                    Timber.d("getRxForecastWeather - $query $result")
                                    result.daily.mapIndexed { index, daily ->
                                        when (index) {
                                            0 -> {
                                            } // we already have data from get current weather API so ignore.
                                            else -> {
                                                if (isNewData) {
                                                    cachedWeather[query]?.add(
                                                        index,
                                                        transform(daily)
                                                    )
                                                } else {
                                                    cachedWeather[query]?.set(
                                                        index,
                                                        transform(daily)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Timber.d("getWeather step 2 -  ${cachedWeather[query]?.size}")
                                    setLoadState(LoadStates.SUCCESS)
                                    setWeatherData(cachedWeather)
                                    if (!disposable.isDisposed) {
                                        disposable.dispose()
                                    }
                                }, {
                                    Timber.e(it)
                                    if (!disposable.isDisposed) {
                                        disposable.dispose()
                                    }
                                })
                        }
                    }
                }
            })
        return cache
    }

    fun setWeatherData(weathers: HashMap<String, MutableList<WeatherUiModel>>) {
        this.cache.value = weathers
    }

    /**
     * Provide live data of load state for observers
     */
    fun getLoadState(): MutableLiveData<LoadStates> {
        return loadState
    }

    /**
     * Checks if the activity is still active, and run the state changes in the UI thread.
     */
    private fun setLoadState(loadState: LoadStates) {
        activity?.runOnUiThread {
            this.loadState.value = loadState
        }
    }

    fun transform(data: WeatherData): WeatherUiModel {
        return WeatherUiModel(
            data.weather[0].main,
            data.main.temp,
            data.main.temp_min,
            data.main.temp_max,
            data.dt,
            data.weather[0].icon
        )
    }

    fun transform(daily: Daily): WeatherUiModel {
        return WeatherUiModel(
            daily.weather[0].main,
            daily.temp.day,
            daily.temp.min,
            daily.temp.max,
            daily.dt,
            daily.weather[0].icon
        )
    }


    fun getCurrentWeather(query: String): LiveData<HashMap<String, MutableList<WeatherUiModel>>> {
        currentWeatherRetrofitService.getCurrentWeather(query)
            .enqueue(object : Callback<WeatherData> {
                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    Timber.d("currentWeather - onFailure")
                }

                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                    Timber.d("currentWeather - onResponse: ${response.body()}")
                    //TODO update cache
                }

            })
        return cache
    }
}