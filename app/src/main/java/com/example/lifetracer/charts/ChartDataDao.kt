package com.example.lifetracer.charts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChartDataDao {
    @Query("SELECT * FROM chart_cw WHERE taskId = :taskId")
    fun getChartDataForTask(taskId: Long): LiveData<List<ChartData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(chartData: ChartData)

    // Other necessary queries
}
