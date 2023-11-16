package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.InstanceWithTask

@Dao
interface InstanceDao {
    @Insert
    fun insert(instance: Instance): Long

    @Update
    fun update(instance: Instance)

    @Delete
    fun delete(instance: Instance)

    @Query("DELETE FROM instances WHERE task_id = :taskId")
    suspend fun deleteInstancesByTaskId(taskId: Long)

    @Query("SELECT * FROM instances")
    fun getAllInstances(): LiveData<List<Instance>>

    @Query("SELECT * FROM instances WHERE id = :instanceId")
    fun getInstanceById(instanceId: Long): Instance?


    // Define a query to retrieve instances along with their associated tasks
    @Transaction
    @Query("SELECT * FROM instances")
    fun getInstancesWithTasks(): LiveData<List<InstanceWithTask>>
}
