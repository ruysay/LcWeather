package com.lc.weather.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherUiModel(
    var condition: String?,
    var temp: Double?,
    var tempMin: Double?,
    var tempMax: Double?,
    var time: Int?,
    var icon: String?
) : Parcelable