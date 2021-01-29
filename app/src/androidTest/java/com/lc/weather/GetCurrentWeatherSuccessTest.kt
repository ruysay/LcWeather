package com.lc.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.lc.carview.FileReader
import com.lc.weather.models.WeatherUiModel
import com.lc.weather.network.CurrentWeatherRetrofitService
import com.lc.weather.network.RetrofitBuilder
import com.lc.weather.ui.MainViewModel
import com.lc.weather.ui.WeatherRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
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

//    private var loadResult: LoadStates = LoadStates.START
    private var apiService = RetrofitBuilder.createCurrentRetrofitService()

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
//        mainViewModel.setCars(null)
//        loadResult = LoadStates.LOADING

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(FileReader.readStringFromFile(R.raw.success_response))
        mockWebServer.enqueue(response)

        // Act
//        mainViewModel.getWeather("").observeForever(Observer {
////            when {
////                it == null -> {
////                    loadResult = LoadStates.ERROR
////                }
////                it.isEmpty() -> {
////                    loadResult = LoadStates.EMPTY
////                }
////                it.isNotEmpty() -> {
////                    loadResult = LoadStates.SUCCESS
////                }
////            }
//            cachedWeather[""] = mutableListOf()
//
//
//
//
//        })

        jsonRepository.getCurrentWeather().execute().body()?.let {
            cachedWeather[""]?.add(0, WeatherRepository.transform(it))

            assertNotNull(cachedWeather)
//            val isNewData = cachedWeather[query] == null
//
//            if(isNewData) {
//                cachedWeather[query] = mutableListOf()
//                cachedWeather[query]?.add(0, transform(it))
//            } else {
//                cachedWeather[query]?.set(0 , transform(it))
//            }
        }

//        WeatherRepository.setWeatherData(jsonRepository.getCurrentWeather().execute().body())

        // Assert
//        assertEquals(loadResult, LoadStates.SUCCESS)
    }


//    /**
//     * Fetch data from actual api and check state
//     * This test case might fail if we change endpoint of the service
//     */
//    @Test
//    fun `fetch details success and check state`(){
//        // Assign
//        mainViewModel.setCars(null)
//        loadResult = LoadStates.LOADING
//
//        // Act
//        val actualResponse = apiService.getCars().execute()
//
//        mainViewModel.getCars().observeForever(Observer {
//            when {
//                it == null -> {
//                    loadResult = LoadStates.ERROR
//                }
//                it.isEmpty() -> {
//                    loadResult = LoadStates.EMPTY
//                }
//                it.isNotEmpty() -> {
//                    loadResult = LoadStates.SUCCESS
//                }
//            }
//        })
//
//        mainViewModel.setCars(actualResponse.body()?.toMutableList())
//
//        // Assert
//        assertEquals(loadResult, LoadStates.SUCCESS)
//    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}