package com.example.lifetracer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lifetracer.data.Task
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.TaskDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var taskDao: TaskDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Allow database operations on the main thread for testing
            .build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertTaskAndGetTaskId() {
        runBlocking {
            val task = Task(0, "Name", "1.1.2023", "2.2.2023", 1, true)
            val taskId = taskDao.insert(task)
            assertNotEquals(0, taskId)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAllTasks() {
            val task1 = Task(0, "Name1", "1.1.2023", "2.2.2023", 1, true)
            val task2 = Task(0, "Name2", "3.3.2023", "4.4.2023", 2, false)

            // Insert the tasks into the database
            runBlocking {
                taskDao.insert(task1)
                taskDao.insert(task2)
            }

            val allTasks = LiveDataTestUtil.getValue(taskDao.getAllTasks())

            // Check if the emitted list is not empty and contains the inserted tasks
            assert(allTasks.isNotEmpty())
            val huhu = "huhu"
    }


}
