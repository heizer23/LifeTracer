package com.example.lifetracer.charts

import androidx.lifecycle.LiveData

class ChartRepository(private val chartDataDao: ChartDataDao) {

        private val historyCache = mutableMapOf<Long, LiveData<List<ChartData>>>()

        fun getHistoryData(taskId: Long): LiveData<List<ChartData>> {
            historyCache[taskId]?.let {
                // Return cached data if available
                return it
            }

            // Data not in cache, fetch from database and update the cache
            val historyData = chartDataDao.getChartDataForTask(taskId)
            historyCache[taskId] = historyData
            return historyData
        }
    }


