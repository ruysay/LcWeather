package com.lc.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lc.carview.enums.LoadStates
import com.lc.carview.network.RetrofitBuilder
import com.lc.carview.ui.MainViewModel
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)

class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel()

        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @Test
    fun `read sample success json file`(){
        val reader = FileReader.readStringFromFile(R.raw.success_response)
        assertNotNull(reader)
    }

    @Test
    fun `test successful response`() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile(R.raw.success_response))
            }
        }
    }

    @Test
    fun `fetch details and check response`(){
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(FileReader.readStringFromFile(R.raw.success_response))
        mockWebServer.enqueue(response)
        // Act
        val actualResponse = RetrofitBuilder.createCarRetrofitService().getCars().execute()
        // Assert
        assertEquals(response.toString().contains("200"),actualResponse.code().toString().contains("200"))
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}