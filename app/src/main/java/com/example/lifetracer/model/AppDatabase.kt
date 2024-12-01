package com.example.lifetracer.model

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lifetracer.charts.ChartData
import com.example.lifetracer.charts.ChartDataDao
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.MainTask
import com.example.lifetracer.data.TaskRelation

@Database(
    entities = [InstanceWithTask::class, TaskRelation::class, ChartData::class],
    views = [MainTask::class], // Room will automatically handle this view
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun instanceDao(): InstanceDao
    abstract fun chartDataDao(): ChartDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "life_tracer_database"
            )
                .fallbackToDestructiveMigration() // Ensure clean migrations for now
                .build()
        }
    }
}
