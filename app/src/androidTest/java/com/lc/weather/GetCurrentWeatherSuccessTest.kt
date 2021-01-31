package com.lc.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.lc.carview.FileReader
import com.lc.weather.models.WeatherUiModel
import com.lc.weather.network.CurrentWeatherRetrofitService
import com.lc.weather.ui.main.MainViewModel
import com.lc.weather.ui.WeatherRepository
import com.lc.weather.enums.LoadStates
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)

class GetCurrentWeatherSuccessTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mockWebServer: MockWebServer

    private var loadResult: LoadStates = LoadStates.START

    lateinit var placeholderApi: CurrentWeatherRetrofitService
    lateinit var jsonRepository: CurrentWeatherJsonRepository

    private val cachedWeather = HashMap<String, MutableList<WeatherUiModel>>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel()

        mockWebServer = MockWebServer()
        mockWebServer.start()

        placeholderApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(CurrentWeatherRetrofitService::class.java)


        val test = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()


        jsonRepository = CurrentWeatherJsonRepository(placeholderApi)
    }

    /**
     * Fetch valid data from mock web server and proxy api and check state
     */
    @Test
    fun `fetch details success from api and check state`() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(FileReader.readStringFromFile(R.raw.success_response))
        mockWebServer.enqueue(response)
        loadResult = LoadStates.LOADING

        // Act
        mainViewModel.getWeatherList().observeForever(Observer { result ->
                result[""]?.toMutableList()?.let { list ->
                    assertTrue(list.size > 0)
                }
        })

        jsonRepository.getCurrentWeather().execute().body()?.let {
            cachedWeather[""]?.add(0, WeatherRepository.transform(it))
            assertNotNull(cachedWeather)
            WeatherRepository.setWeatherData(cachedWeather)
            loadResult = LoadStates.SUCCESS

        }
        assertEquals(loadResult, LoadStates.SUCCESS)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}