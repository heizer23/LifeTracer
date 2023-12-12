package com.example.lifetracer.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lifetracer.charts.ChartDao
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.Task

@Database(entities = [Task::class, Instance::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun instanceDao(): InstanceDao
    abstract fun chartDao(): ChartDao

    companion object {
        // Using 'by lazy' to ensure thread-safe initialization of the database instance
        private val LOCK = Any()
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(LOCK) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).fallbackToDestructiveMigration().build()
        }
    }
}

