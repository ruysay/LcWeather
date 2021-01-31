package com.lc.weather.ui.longforecast

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
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
import com.lc.weather.R
import com.lc.weather.enums.LoadStates
import com.lc.weather.models.WeatherUiModel
import com.lc.weather.ui.main.MainActivity
import com.lc.weather.ui.main.MainViewModel
import com.lc.weather.utils.ConditionIconUtil
import kotlinx.android.synthetic.main.fragment_weather.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class LocationWeatherFragment(private var city: String? = null) : Fragment(), LocationListener {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(context, Locale.getDefault())

        adapter =
            ForecastAdapter(
                view.context
            )
        listView = view.findViewById<ListView>(R.id.weather_3days_forecast)
        listView.adapter = adapter

        setUI()

        mainViewModel.getLoadState().observe(viewLifecycleOwner, Observer { loadState ->
            when (loadState) {
                LoadStates.START, LoadStates.LOADING -> {
                    main_progress_bar.visibility = VISIBLE
                }
                LoadStates.SUCCESS -> {
                    main_progress_bar.visibility = GONE
                    weather_long_forecast_button.visibility = VISIBLE
                }
                LoadStates.ERROR -> {
                    main_progress_bar.visibility = GONE
                    Toast.makeText(context, R.string.msg_load_failed, Toast.LENGTH_LONG).show()
                }
                LoadStates.EMPTY -> {
                    main_progress_bar.visibility = GONE
                    Toast.makeText(context, R.string.msg_load_empty_result, Toast.LENGTH_LONG)
                        .show()

                }
                else -> {
                }
            }
        })

        mainViewModel.getWeatherList().observe(viewLifecycleOwner, Observer { result ->
            activity?.runOnUiThread {
                main_progress_bar.visibility = GONE
                result[city]?.toMutableList()?.let { list ->
                    list[0].let { weather ->
                        weather_temp_range.text = getString(
                            R.string.temp_range,
                            weather.tempMax?.toInt(),
                            weather.tempMin?.toInt()
                        )
                        weather_condition.text = weather.condition
                        weather_current_temp.text =
                            getString(R.string.temp_now, weather.temp?.toInt())
                    }

                    if (list.size > 4) {
                        adapter.setList(list.subList(1, 4))
                    }
                }
                weather_location_name.text = city
            }
        })
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

        if (!haveSetUI && city == null) {
            haveSetUI = true
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || !haveAskedPermission) {
                    haveAskedPermission = true
                    showPermissionDialog(context)
                }
            } else {
                //get weather
                checkLastKnownLocation()
            }
        }
        if (city != null) mainViewModel.getWeather(city!!)
    }

    private fun setUI() {
        // set background photo based on city name
        weather_layout_background.setImageResource(
            when (city) {
                "Sydney" -> {
                    R.drawable.photo_sydney
                }
                "Perth" -> {
                    R.drawable.photo_perth
                }
                "Hobart" -> {
                    R.drawable.photo_hobart
                }
                else -> R.drawable.photo_home
            }
        )

        // set long forecast action
        weather_long_forecast_button.setOnClickListener {
            val intent = Intent(activity, LongForecastActivity::class.java)
            val cityAddress = geocoder.getFromLocationName(city, 1)
            cityAddress?.get(0)?.let {
                if (it.hasLatitude() && it.hasLongitude()) {
                    val latLng = LatLng(it.latitude, it.longitude)
                    intent.putExtra("latLng", latLng)
                    intent.putExtra("city", city)
                    activity?.startActivity(intent)
                } else {
                    Toast.makeText(activity, "Not able to handle the request", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    // request permission to get current location
    private fun showPermissionDialog(context: Context) {
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.location_permission_alert_title)
        dialog.setMessage(R.string.location_permission_alert_message)
        dialog.setPositiveButton(R.string.common_confirm) { _, _ ->
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        dialog.setNegativeButton(R.string.common_cancel) { _, _ ->
            Toast.makeText(context, R.string.location_permission_alert_message, Toast.LENGTH_LONG)
                .show()
            showPermissionDialog(context)
        }
        dialog.show()
    }

    private fun checkLastKnownLocation() {
        val context = this.context ?: return

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
                            }
                        }

                        isNetworkEnabled == true -> {
                            if (::locationManager.isInitialized) {
                                //requestLocationUpdates
                                locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_FOR_UPDATE,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATE, this
                                )
                            }
                            // use locationManager's last known location
                            lastLocation =
                                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        }
                    }
                    if (lastLocation != null) {
                        updateLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
                    } else {
                        Timber.d("checkWeather last known location not found")
                        // use default location
                        updateLocation(LatLng(-37.7251, 144.938))
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

    // Adapter for simple list view to present following 3 days weather summary
    class ForecastAdapter(context: Context) : BaseAdapter() {

        private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
                holder =
                    ViewHolder()
                holder.conditionIcon =
                    view.findViewById(R.id.weather_forecast_condition_icon) as ImageView
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
                dateTxt.text = simpleDateFormat.format(Date(it.toLong() * 1000))
            }

            tempRangeTxt.text = view.context.getString(
                R.string.temp_range,
                weatherData.tempMax?.toInt(),
                weatherData.tempMin?.toInt()
            )
            conditionIcon.setImageResource(ConditionIconUtil.getDrawable(weatherData.icon!!))
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
            if (it.size > 0) {
                val city = "${it[0].locality},${it[0].countryName}"
                this.city = city
                mainViewModel.getWeather(city)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        updateLocation(LatLng(location.latitude, location.longitude))
    }
}