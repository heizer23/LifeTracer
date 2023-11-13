package com.example.lifetracer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.Task
import com.example.lifetracer.model.AppDatabase
import com.example.lifetracer.model.InstanceDao
import com.example.lifetracer.model.TaskDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class InstanceDaoTest {
    private lateinit var instanceDao: InstanceDao
    private lateinit var taskDao: TaskDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Allow database operations on the main thread for testing
            .build()
        instanceDao = db.instanceDao()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertInstanceAndGetInstanceId() {
        runBlocking {
            val instance = Instance(0, 1, "2.2.2023", "13:00:13", 0, 1, 0, "comment", "huhu", 0)
            val instanceId = instanceDao.insert(instance)
            assertNotEquals(0, instanceId)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAllInstances() {
        val instance1 = Instance(0, 1, "2.2.2023", "13:00:13", 0, 1, 0, "comment", "huhu", 0)
        val instance2 = Instance(0, 2, "2.3.2023", "01:00:13", 0, 1, 0, "comment", "huhu", 0)

        // Insert the instances into the database
        runBlocking {
            instanceDao.insert(instance1)
            instanceDao.insert(instance2)
        }

        val allInstances = LiveDataTestUtil.getValue(instanceDao.getAllInstances())

        // Check if the emitted list is not empty and contains the inserted instances
        assert(allInstances.isNotEmpty())
        assertEquals(2, allInstances[1].id)
    }

    @Test
    @Throws(Exception::class)
    fun getAllInstancesWithTask() {

        val task = Task(0, "Name", "1.1.2023", "2.2.2023", 1, true)
        var taskId: Long


        runBlocking {
            taskId = taskDao.insert(task)
        }


        val instance1 = Instance(0, taskId, "2.2.2023", "13:00:13", 0, 1, 0, "comment", "huhu", 0)
        val instance2 = Instance(0, taskId, "2.3.2023", "01:00:13", 0, 1, 0, "comment", "huhu", 0)

        // Insert the instances into the database
        runBlocking {
            instanceDao.insert(instance1)
            instanceDao.insert(instance2)
        }

        val allInstancesWithTask = LiveDataTestUtil.getValue(instanceDao.getInstancesWithTasks())

        // Check if the emitted list is not empty and contains the inserted instances
        assert(allInstancesWithTask.isNotEmpty())
        assertEquals(1, allInstancesWithTask[1].task.taskId)
    }

}
