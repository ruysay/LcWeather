package com.lc.weather.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lc.weather.ui.longforecast.LocationWeatherFragment

class MainActivityPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // List of MainVideoFragments
    // Note: Can contain other types of fragments
    private var fragments: MutableList<Fragment> = mutableListOf()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    /**
     * Clear all fragments and create new ones with the stream URL
     * @param streams the video stream URLs
     */
    fun setCities(cities: List<String>) {
        fragments.clear()

        cities.forEachIndexed{ index, city ->
            when(index) {
                0 -> {
                    fragments.add(
                        LocationWeatherFragment(
                            null
                        )
                    )
                }
                else -> {
                    fragments.add(
                        LocationWeatherFragment(
                            city
                        )
                    )
                }
            }
        }
        notifyDataSetChanged()
//        val fragment1 = MainVideoFragment()
//        fragment1.arguments = createBundleStream(streams.screen1)
//
//        val fragment2 = MainVideoFragment()
//        fragment2.arguments = createBundleStream(streams.screen2)
//
//        val fragment3 = MainVideoFragment()
//        fragment3.arguments = createBundleStream(streams.screen3)
//
//        fragments.add(fragment1)
//        fragments.add(fragment2)
//        fragments.add(fragment3)
//
//        notifyDataSetChanged()
    }

    /**
     * Helper function to create a bundle
     * @param stream URL
     */
    private fun createBundleStream(stream: String?): Bundle {
        val bundle = Bundle()
        bundle.putString("STREAM_URL", stream)
        return bundle
    }
}