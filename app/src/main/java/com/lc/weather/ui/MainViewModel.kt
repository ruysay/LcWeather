package com.lc.carview.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lc.carview.models.CarData
import com.lc.carview.enums.LoadStates

class MainViewModel : ViewModel() {
    private val repository = MainRepository

    fun init(context: Context) {
        repository.init(context)
    }

    fun getCars(): LiveData<List<CarData>> {
        return repository.getCars()
    }

    fun setCars(cars: MutableList<CarData>? = getCars().value?.toMutableList()) {
        repository.setCarData(cars)
    }

    fun getLoadState(): MutableLiveData<LoadStates> {
        return repository.getLoadState()
    }
}