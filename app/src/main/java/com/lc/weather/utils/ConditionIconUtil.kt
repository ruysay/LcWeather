package com.lc.weather.utils

import com.lc.weather.R

class ConditionIconUtil {
    companion object {
        fun getDrawable(name: String) : Int {
            return when(name) {
                "01d" -> R.drawable.a01d_svg
                "01n" -> R.drawable.a01n_svg
                "02d" -> R.drawable.a02d_svg
                "02n" -> R.drawable.a02n_svg
                "03d" -> R.drawable.a03d_svg
                "03n" -> R.drawable.a03n_svg
                "04d" -> R.drawable.a04d_svg
                "04n" -> R.drawable.a04n_svg
                "09d" -> R.drawable.a09d_svg
                "09n" -> R.drawable.a09n_svg
                "10d" -> R.drawable.a10d_svg
                "10n" -> R.drawable.a10n_svg
                "11d" -> R.drawable.a11d_svg
                "11n" -> R.drawable.a11n_svg
                "1232n" -> R.drawable.a1232n_svg
                "13d" -> R.drawable.a13d_svg
                "13n" -> R.drawable.a13n_svg
                "50d" -> R.drawable.a50d_svg
                "50n" -> R.drawable.a50n_svg
                else -> R.drawable.ic_unavailable
            }
        }
    }
}