package com.lc.weather.ui.longforecast

import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.lc.weather.R
import com.lc.weather.enums.LoadStates
import kotlinx.android.synthetic.main.activity_long_forecast.*

class LongForecastActivity : AppCompatActivity() {

    private lateinit var longForecastViewModel: LongForecastViewModel
    private lateinit var latLng: LatLng
    private lateinit var city: String

    private val adapter: LongForecastAdapter by lazy {
        LongForecastAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_long_forecast)

        // get latLng and city from intent
        latLng = intent.getParcelableExtra("latLng")!!
        city = intent.getStringExtra("city")!!

        setViewModels()
    }

    override fun onStart() {
        super.onStart()

        setUI()

        longForecastViewModel.getLongDurationForecast(city, latLng).observe(this, Observer { result ->
            //update recycler view
            result[city]?.toMutableList()?.let { list ->
                adapter.setList(list)
                adapter.notifyDataSetChanged()
            }
            //TODO use DiffUtil to update only what is changed
        })

        // update UI based on load state
        longForecastViewModel.getLoadState().observe(this, Observer { loadState ->
            when (loadState) {
                LoadStates.START, LoadStates.LOADING -> {
                    long_forecast_recycler_view.visibility = View.GONE
                    long_forecast_info.visibility = View.VISIBLE
                    long_forecast_info.text = getString(R.string.msg_loading_data)
                }
                LoadStates.SUCCESS -> {
                    long_forecast_info.text = getString(R.string.msg_load_complete)
                    TransitionManager.beginDelayedTransition(long_forecast_ui_container)
                    long_forecast_recycler_view.visibility = View.VISIBLE
                    long_forecast_info.visibility = View.INVISIBLE
                }
                LoadStates.ERROR -> {
                    long_forecast_info.text = getString(R.string.msg_load_failed)
                    long_forecast_recycler_view.visibility = View.GONE
                    long_forecast_info.visibility = View.VISIBLE
                }
                LoadStates.EMPTY -> {
                    long_forecast_info.text = getString(R.string.msg_load_empty_result)
                    long_forecast_recycler_view.visibility = View.GONE
                    long_forecast_info.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        })

        // configure adapter
        long_forecast_recycler_view.adapter = adapter
        long_forecast_recycler_view.layoutManager = LinearLayoutManager(this)
    }

    private fun setUI() {
        long_forecast_layout_background.setImageResource(when(city) {
            "Sydney" -> {R.drawable.photo_sydney}
            "Perth" -> {R.drawable.photo_perth}
            "Hobart" -> {R.drawable.photo_hobart}
            else -> R.drawable.photo_home
        })
        long_forecast_location_name.text = city
    }

    private fun setViewModels() {
        longForecastViewModel = ViewModelProvider(this).get(LongForecastViewModel::class.java)
        longForecastViewModel.init(this)
    }
}