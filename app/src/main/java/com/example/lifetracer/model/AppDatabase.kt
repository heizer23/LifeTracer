package com.example.lifetracer.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.Task

@Database(entities = [Task::class, Instance::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    // Define DAOs for your entities
    abstract fun taskDao(): TaskDao
    abstract fun instanceDao(): InstanceDao

    companion object {
        // Singleton instance of the database
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Get or create the database instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
