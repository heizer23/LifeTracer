package com.example.lifetracer.charts

import androidx.lifecycle.LiveData
import com.example.lifetracer.model.AppDatabase

class ChartRepository(private val chartDao: ChartDao) {

    fun getBarChartData(): LiveData<List<BarEntryData>> {
        return chartDao.getBarChartData()
    }

}
