package com.lc.weather.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherUiModel(
    var condition: String?,
    var temp: Double?,
    var tempMin: Double?,
    var tempMax: Double?,
    var time: Int?
) : Parcelable


/*
{
    "coord": {
        "lon": 115.8333,
        "lat": -31.9333
    },
    "weather": [
        {
            "id": 800,
            "main": "Clear",
            "description": "clear sky",
            "icon": "01d"
        }
    ],
    "base": "stations",
    "main": {
        "temp": 19,
        "feels_like": 12.79,
        "temp_min": 18.33,
        "temp_max": 19.44,
        "pressure": 1015,
        "humidity": 59
    },
    "visibility": 10000,
    "wind": {
        "speed": 9.26,
        "deg": 110,
        "gust": 14.4
    },
    "clouds": {
        "all": 0
    },
    "dt": 1611873293,
    "sys": {
        "type": 1,
        "id": 9586,
        "country": "AU",
        "sunrise": 1611869930,
        "sunset": 1611919229
    },
    "timezone": 28800,
    "id": 2063523,
    "name": "Perth",
    "cod": 200
}
 */