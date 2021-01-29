package com.lc.carview

import com.lc.carview.models.CarData
import com.lc.carview.network.CarsRetrofitService
import retrofit2.Call

/**
 * A proxy to test API which is served by MockServer
 */
class JsonRepository(private val api: CarsRetrofitService) {
    fun getValidCarList(): Call<List<CarData>> = api.getCars()
    fun getEmptyCarList(): Call<List<CarData>> = api.getEmptyCars()
    fun getInvalidCarList(): Call<List<CarData>> = api.getInvalidCars()
}