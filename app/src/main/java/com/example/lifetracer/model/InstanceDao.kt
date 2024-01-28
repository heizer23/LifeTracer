package com.example.lifetracer.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.lifetracer.data.InstanceWithTask
import com.example.lifetracer.data.TaskRelation

@Dao
interface InstanceDao {
    @Insert
    fun insert(instance: InstanceWithTask): Long

    @Update
    fun update(instance: InstanceWithTask)

    @Delete
    fun delete(instance: InstanceWithTask)


    @Query("SELECT * FROM instances")
    fun getAllInstances(): LiveData<List<InstanceWithTask>>

    @Query("SELECT * FROM instances WHERE id = :instanceId")
    fun getInstanceById(instanceId: Long): InstanceWithTask?

    @Transaction
    @Query("SELECT * FROM instances WHERE id = :instanceId")
    suspend fun getInstanceWithTask(instanceId: Long): InstanceWithTask


    @Query("UPDATE instances SET priority = :priority WHERE id = :instanceId")
    suspend fun updatePrio(instanceId: Long, priority: Int)

    // Define a query to retrieve instances along with their associated tasks
    @Transaction
    @Query("SELECT * FROM instances WHERE status != 99 order by priority")
    fun getActiveInstancesWithTasks(): LiveData<List<InstanceWithTask>>

    @Transaction
    @Query("SELECT * FROM instances WHERE status != 99 ORDER BY priority LIMIT 1")
    fun getLowestPriorityInstanceWithTask(): LiveData<InstanceWithTask>

    // Insert relationship
    @Insert
    suspend fun insertTaskRelation(taskRelation: TaskRelation)

    // Query subtasks for a specific parent
    @Query("SELECT instances.* FROM instances INNER JOIN task_relation ON instances.id = task_relation.subtaskId WHERE task_relation.parentId = :parentId")
    suspend fun getSubtasksForParent(parentId: Long): List<InstanceWithTask>


}
