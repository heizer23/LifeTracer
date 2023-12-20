package com.example.lifetracer.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChartRepository(private val chartDataDao: ChartDataDao) {

    private val chartDataCache = mutableMapOf<Long, List<ChartData>>()

    //todo List = Livedata?
    suspend fun getChartData(taskId: Long): List<BarEntry> {
        chartDataCache[taskId]?.let { cachedData ->
            // Return cached data if available
            return cachedData.map { chartData ->
                BarEntry(chartData.calendarWeek, chartData.numberOfInstances)
            }
        }
        // Fetch from database and update the cache
        val fetchedData = withContext(Dispatchers.IO) {
            chartDataDao.getChartDataForTask(taskId)
        }
        chartDataCache[taskId] = fetchedData

        return fetchedData.map { chartData ->
            BarEntry(chartData.calendarWeek, chartData.numberOfInstances)
        }

    }

    fun updateChartData(taskId: Long, scope: CoroutineScope) {
        scope.launch {
            val aggregatedData = chartDataDao.getAggregatedDataForTask(taskId)
            aggregatedData.forEach { data ->
                chartDataDao.insertOrUpdate(data)
            }
        }
    }

    }


