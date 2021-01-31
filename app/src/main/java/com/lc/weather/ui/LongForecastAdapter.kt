package com.lc.weather.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lc.weather.LcWeatherApplication
import com.lc.weather.R
import com.lc.weather.models.WeatherUiModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LongForecastAdapter() :
    RecyclerView.Adapter<LongForecastAdapter.CarViewHolder>() {

    private var weathers: MutableList<WeatherUiModel> = mutableListOf()

    fun setList(carList: MutableList<WeatherUiModel>?) {
        //TODO use DiffUtil to update only what is changed
        this.weathers = carList ?: mutableListOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        return CarViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_holder_weather_forecast_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return weathers.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.onBind(position, weathers)
    }


    class CarViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val container: LinearLayout = view.findViewById(R.id.weather_forecast_list_container)
        private val date: TextView = view.findViewById(R.id.weather_forecast_date)
        private val tempRange: TextView = view.findViewById(R.id.weather_forecast_temp_range)
        private val conditionIcon: ImageView = view.findViewById(R.id.weather_forecast_condition_icon)

        private val simpleDateFormat = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int, weatherList: List<WeatherUiModel>) {
            val weatherData = weatherList[position]
            container.setOnClickListener {
                val clickAnimation = AlphaAnimation(0.3f, 1.0f)
                clickAnimation.duration = 150
                container.startAnimation(clickAnimation)
            }

            weatherData.time?.let {
                date.text = simpleDateFormat.format(Date(it.toLong()*1000))
            }
            tempRange.text = view.context.getString(R.string.temp_range, weatherData.tempMax?.toInt(), weatherData.tempMin?.toInt() )

            val imgUrl = "https://www.weatherbit.io/static/img/icons/${weatherData.icon}.png"
            Timber.d("checkImg: $imgUrl")
            LcWeatherApplication.picassoWithCache?.load(imgUrl)?.placeholder(R.drawable.progress_animation)?.into(conditionIcon)

        }
    }
}