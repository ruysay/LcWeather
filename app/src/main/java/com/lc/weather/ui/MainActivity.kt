package com.lc.weather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.lc.weather.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: MainActivityPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewModels()

        // Setup ViewPagerAdapter
        adapter = MainActivityPagerAdapter(supportFragmentManager)
        main_view_pager.adapter = adapter
        main_video_control.setupWithViewPager(main_view_pager)
    }

    override fun onStart() {
        super.onStart()

        val cities = mutableListOf<String>()
        cities.add("Current")
        cities.add("Sydney")
        cities.add("Perth")
        cities.add("Hobart")
        adapter.setCities(cities)
    }

    private fun setViewModels() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.init(this)
    }
}
