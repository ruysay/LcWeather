package com.lc.carview

import androidx.test.platform.app.InstrumentationRegistry
import com.lc.weather.LcWeatherApplication
import java.io.IOException
import java.io.InputStreamReader


object FileReader {
    fun readStringFromFile(fileId: Int): String {
        try {
            val inputStream = (InstrumentationRegistry.getInstrumentation().targetContext
                .applicationContext as LcWeatherApplication).resources.openRawResource(fileId)
            val builder = StringBuilder()
            val reader = InputStreamReader(inputStream, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }
            return builder.toString()
        } catch (e: IOException) {
            throw e
        }
    }
}