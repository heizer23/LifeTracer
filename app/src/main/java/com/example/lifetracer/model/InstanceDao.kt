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


    @Transaction
    @Query("SELECT * FROM instances WHERE id = :instanceId")
    suspend fun getInstanceById(instanceId: Long): InstanceWithTask


    @Query("UPDATE instances SET priority = :priority WHERE id = :instanceId")
    suspend fun updatePrio(instanceId: Long, priority: Int)



    // Fill Listviews----------------------------------------------------------------------------

    // Main View
    @Transaction
    @Query("SELECT * FROM main_tasks WHERE status != 99 AND status != 98 ORDER BY priority")
    fun getActiveInstances(): LiveData<List<InstanceWithTask>>

    //Vault View
    @Query("""
    SELECT * FROM main_tasks 
    WHERE status = :vaultStatus 
      AND id NOT IN (
          SELECT templateId 
          FROM instances 
          WHERE status NOT IN (:vaultStatus, :finishedStatus)
      )
    ORDER BY regularity ASC, date_of_creation ASC
""")
    fun getVaultedTasks(
        vaultStatus: Int = InstanceWithTask.STATUS_VAULTED,
        finishedStatus: Int = InstanceWithTask.STATUS_FINISHED
    ): LiveData<List<InstanceWithTask>>

    // Review View
    @Query("SELECT * FROM main_tasks WHERE status = :finishedStatus AND date = :currentDate")
    fun getFinishedInstancesForDay(finishedStatus: Int, currentDate: String): LiveData<List<InstanceWithTask>>

    @Transaction
    @Query("SELECT * FROM main_tasks WHERE status < 98 ORDER BY priority LIMIT 1")
    fun getLowestPriorityInstance(): LiveData<InstanceWithTask>

    @Transaction
    @Query("""
    SELECT t.*
    FROM instances t
    LEFT JOIN main_tasks m ON t.id = m.id
    INNER JOIN task_relation tr ON t.id = tr.subtaskId
    WHERE m.id IS NULL AND tr.parentId = :parentId
    ORDER BY t.priority
    LIMIT 1
""")
    fun getLowestPrioritySubTask(parentId: Long): LiveData<InstanceWithTask>




    // Insert relationship
    @Insert
    suspend fun insertTaskRelation(taskRelation: TaskRelation)

    // Query subtasks for a specific parent
    @Query("SELECT instances.* FROM instances INNER JOIN task_relation ON instances.id = task_relation.subtaskId WHERE task_relation.parentId = :parentId")
    suspend fun getSubtasksForParent(parentId: Long): List<InstanceWithTask>


}
