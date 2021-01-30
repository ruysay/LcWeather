package com.lc.weather.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.lc.weather.LcWeatherApplication
import com.lc.weather.R
import com.lc.weather.models.WeatherUiModel
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class LocationWeatherFragment(private val useCurrentLocation: Boolean = false) : Fragment(),
    LocationListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val MIN_TIME_FOR_UPDATE: Long = 5000
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATE: Float = 0f
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var listView: ListView

    private lateinit var adapter: ForecastAdapter

    // Location info
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isGpsEnabled: Boolean? = false
    private var isNetworkEnabled: Boolean? = false
    private lateinit var locationManager: LocationManager
    private var haveAskedPermission = false
    private var myLocation: LatLng? = null
    private var haveSetUI = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val mainActivity = activity as MainActivity
        mainViewModel = ViewModelProvider(mainActivity).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(context, Locale.getDefault())

        adapter = ForecastAdapter(view.context)
        listView = view.findViewById<ListView>(R.id.weather_3days_forecast)
        listView.adapter = adapter

        mainViewModel.getWeather("melbourne,au").observe(viewLifecycleOwner, Observer {
            Timber.d("checkWeather observed: ${it["melbourne,au"]?.size}")
            adapter.setList(it["melbourne,au"]?.toMutableList())
        })

        Timber.d("useCurrentLocation: $useCurrentLocation")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLastKnownLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val context = this.context ?: return
        if (!haveSetUI) {
            haveSetUI = true
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) || !haveAskedPermission) {
                    haveAskedPermission = true
                    showPermissionDialog(context)
                }
            } else {
                //get weather
                checkLastKnownLocation()
            }
        }
    }

    private fun showPermissionDialog(context: Context) {
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.location_permission_alert_title)
        dialog.setMessage(R.string.location_permission_alert_message)
        dialog.setPositiveButton(R.string.common_confirm) { _, _ ->
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        dialog.setNegativeButton(R.string.common_cancel) { _, _ ->
            Toast.makeText(context, R.string.location_permission_alert_message, Toast.LENGTH_LONG).show()
            showPermissionDialog(context)
        }
        dialog.show()
    }

    private fun checkLastKnownLocation() {
        val context = this.context ?: return

//        TransitionManager.beginDelayedTransition(search_location_container)
//        explore_location_map.visibility = View.VISIBLE

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Location may be null
            try {
                if (location == null) {
                    var lastLocation = location
                    when {
                        // Use GPS first if available
                        isGpsEnabled == true -> {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                if (::locationManager.isInitialized) {
                                    //requestLocationUpdates
                                    locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_FOR_UPDATE,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATE,
                                        this
                                    )
                                }
                                // use locationManager's last known location
                                lastLocation = locationManager.getLastKnownLocation(
                                    LocationManager.GPS_PROVIDER
                                )
                                Timber.d("lastLocation from GPS: ${lastLocation?.latitude}, ${lastLocation?.longitude}")
                            }
                        }

                        isNetworkEnabled == true -> {
                            if (::locationManager.isInitialized) {
                                //requestLocationUpdates
                                locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this
                                )
                            }
                            // use locationManager's last known location
                            lastLocation =
                                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            Timber.d("lastLocation from NETWORK: ${lastLocation?.latitude}, ${lastLocation?.longitude}")
                        }
                    }
                    if (lastLocation != null) {
                        Timber.d("checkWeather got last known location: ${lastLocation.latitude}, ${lastLocation.longitude}")
                        updateLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
                    } else {
                        Timber.d("checkWeather last known location not found")

                        updateLocation(LatLng(-37.81, 144.96))
                    }

                } else {
                    Timber.d("lastLocation from fusedLocationClient: ${location.latitude}, ${location.longitude}")
                    updateLocation(LatLng(location.latitude, location.longitude))
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    class ForecastAdapter(private val context: Context) : BaseAdapter() {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        private var dataSource: MutableList<WeatherUiModel> = mutableListOf()

        private val simpleDateFormat = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())

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

            weatherData.time?.let {
                dateTxt.text = simpleDateFormat.format(Date(it.toLong()*1000))
            }

            tempRangeTxt.text = view.context.getString(R.string.temp_range, weatherData.tempMax?.toInt(), weatherData.tempMin?.toInt() )

            val imgUrl = "http://openweathermap.org/img/w/${weatherData.icon}.png"
            LcWeatherApplication.picassoWithCache?.load(imgUrl)?.placeholder(R.drawable.gray_background)?.fit()?.into(conditionIcon)
            return view
        }

        private class ViewHolder {
            lateinit var date: TextView
            lateinit var tempRange: TextView
            lateinit var conditionIcon: ImageView
        }
    }

    private fun updateLocation(latLng: LatLng) {
        myLocation = latLng
        val locationInfo = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        locationInfo?.let {
            if(it.size > 0) {
                val city = it[0].locality
                Timber.d("checkWeather - updateLocation: let's get weather with latlng: $city")

            }
        }
        Timber.d("checkWeather - updateLocation: let's get weather with latlng")
    }

    override fun onLocationChanged(location: Location) {
        updateLocation(LatLng(location.latitude, location.longitude))
    }
}