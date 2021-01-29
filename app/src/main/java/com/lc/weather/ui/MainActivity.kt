package com.lc.weather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lc.weather.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewModels()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.getWeather("melbourne,au").observe(this, Observer {
            Timber.d("checkWeather observed: ${it["melbourne,au"]?.size}")
        })
//        mainViewModel.getForecastWeather()
    }
    private fun setViewModels() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.init(this)
    }
}
