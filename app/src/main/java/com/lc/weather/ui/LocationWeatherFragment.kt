package com.lc.weather.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lc.weather.LcWeatherApplication
import com.lc.weather.R
import com.lc.weather.models.WeatherUiModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_weather.*
import timber.log.Timber


class LocationWeatherFragment(private val useCurrentLocation: Boolean = false) : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var listView: ListView

    private lateinit var adapter: ForecastAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val mainActivity = activity as MainActivity
        mainViewModel = ViewModelProvider(mainActivity).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        adapter = ForecastAdapter(view.context)
        listView = view.findViewById<ListView>(R.id.weather_3days_forecast)
        listView.adapter = adapter

        mainViewModel.getWeather("melbourne,au").observe(viewLifecycleOwner, Observer {
            Timber.d("checkWeather observed: ${it["melbourne,au"]?.size}")
            adapter.setList(it["melbourne,au"]?.toMutableList())
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("useCurrentLocation: $useCurrentLocation")
    }

    class ForecastAdapter(private val context: Context) : BaseAdapter() {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        private var dataSource: MutableList<WeatherUiModel> = mutableListOf()

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        fun setList(weatherList: MutableList<WeatherUiModel>?) {
            this.dataSource = weatherList ?: mutableListOf()
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View
            val holder: ViewHolder

            if (convertView == null) {
                view = inflater.inflate(R.layout.view_holder_weather_forecast_list, parent, false)
                holder = ViewHolder()
                holder.conditionIcon = view.findViewById(R.id.weather_forecast_condition_icon) as ImageView
                holder.date = view.findViewById(R.id.weather_forecast_date) as TextView
                holder.tempRange = view.findViewById(R.id.weather_forecast_temp_range) as TextView
                view.tag = holder
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }

            val dateTxt = holder.date
            val tempRangeTxt = holder.tempRange
            val conditionIcon = holder.conditionIcon

            val weatherData = getItem(position) as WeatherUiModel

            dateTxt.text = weatherData.time.toString()
            tempRangeTxt.text = weatherData.tempMax.toString() + " /" +  weatherData.tempMin.toString()

            LcWeatherApplication.picassoWithCache?.load(R.drawable.ic_launcher_background)?.placeholder(R.drawable.ic_launcher_background)?.fit()?.into(conditionIcon)
            return view
        }

        private class ViewHolder {
            lateinit var date: TextView
            lateinit var tempRange: TextView
            lateinit var conditionIcon: ImageView
        }
    }
}