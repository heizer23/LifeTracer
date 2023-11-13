package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask

class InstanceRepository(private val instanceDao: InstanceDao) {

    // LiveData to observe instances with associated tasks
    val allInstancesWithTasks: LiveData<List<InstanceWithTask>> = instanceDao.getInstancesWithTasks()

    // Insert an instance
    suspend fun insert(instance: Instance) {
        instanceDao.insert(instance)
    }

    suspend fun addEmptyInstance(taskId: Long, date: String, time: String) {
        val instance = Instance(
            taskId = taskId,
            date = date,
            time = time,
            duration = 0,
            totalPause = 0,
            quantity = 0,
            quality = "",
            comment = "",
            status = 0
        )
        insert(instance)
    }


}
