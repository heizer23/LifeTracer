package com.example.lifetracer.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry

class ChartViewModel : ViewModel() {

    private val _chartData = MutableLiveData<List<BarEntry>>()
    val chartData: LiveData<List<BarEntry>> = _chartData

    init {
        loadChartData()
    }

    private fun loadChartData() {
        // Load or generate chart data
        val data = listOf(
            BarEntry(1f, 10f),
            BarEntry(2f, 20f),
            BarEntry(4f, 20f),
            BarEntry(5f, 20f),
            BarEntry(8f, 20f),
            BarEntry(12f, 20f),
            BarEntry(15f, 20f)
        )
        _chartData.value = data
    }

    // Add other methods to manipulate chart data as needed
}
