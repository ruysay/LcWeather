package com.lc.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.lc.carview.enums.LoadStates
import com.lc.carview.network.CarsRetrofitService
import com.lc.carview.network.RetrofitBuilder
import com.lc.carview.ui.MainViewModel
import junit.framework.TestCase.assertEquals
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
    private var apiService = RetrofitBuilder.createCarRetrofitService()

    lateinit var placeholderApi: CarsRetrofitService
    lateinit var jsonRepository: JsonRepository

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
            .create(CarsRetrofitService::class.java)


        val test = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()


        jsonRepository = JsonRepository(placeholderApi)
    }

    /**
     * Fetch valid data from mock web server and proxy api and check state
     */
    @Test
    fun `fetch details success from api and check state`() {
        // Assign
        mainViewModel.setCars(null)
        loadResult = LoadStates.LOADING

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(FileReader.readStringFromFile(R.raw.success_response))
        mockWebServer.enqueue(response)

        // Act
        mainViewModel.getCars().observeForever(Observer {
            when {
                it == null -> {
                    loadResult = LoadStates.ERROR
                }
                it.isEmpty() -> {
                    loadResult = LoadStates.EMPTY
                }
                it.isNotEmpty() -> {
                    loadResult = LoadStates.SUCCESS
                }
            }
        })

        mainViewModel.setCars(jsonRepository.getValidCarList().execute().body()?.toMutableList())

        // Assert
        assertEquals(loadResult, LoadStates.SUCCESS)
    }


    /**
     * Fetch data from actual api and check state
     * This test case might fail if we change endpoint of the service
     */
    @Test
    fun `fetch details success and check state`(){
        // Assign
        mainViewModel.setCars(null)
        loadResult = LoadStates.LOADING

        // Act
        val actualResponse = apiService.getCars().execute()

        mainViewModel.getCars().observeForever(Observer {
            when {
                it == null -> {
                    loadResult = LoadStates.ERROR
                }
                it.isEmpty() -> {
                    loadResult = LoadStates.EMPTY
                }
                it.isNotEmpty() -> {
                    loadResult = LoadStates.SUCCESS
                }
            }
        })

        mainViewModel.setCars(actualResponse.body()?.toMutableList())

        // Assert
        assertEquals(loadResult, LoadStates.SUCCESS)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}