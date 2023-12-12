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




    @Query("UPDATE instances SET priority = :priority WHERE id = :instanceId")
    suspend fun updatePrio(instanceId: Long, priority: Int)

    // Define a query to retrieve instances along with their associated tasks
    @Transaction
    @Query("""
        SELECT
            t.name,
            ci.*,
            GROUP_CONCAT(hi.date || '_' || hi.quantity) AS historical_data
        FROM
            tasks t
        LEFT JOIN
            instances ci ON t.task_id = ci.task_id AND ci.status <> 99  -- Current instance
        LEFT JOIN
            instances hi ON t.task_id = hi.task_id AND hi.status = 99  -- Historical instances
        WHERE
             ci.id is not NULL
        GROUP BY
            t.task_id;
            """)
    fun getActiveInstancesWithTasks(): LiveData<List<InstanceWithTask>>

    @Transaction
    @Query("""
        SELECT
            t.name,
            ci.*,
            GROUP_CONCAT(hi.date || '_' || hi.quantity) AS historical_data
        FROM
            tasks t
        INNER JOIN (
            SELECT * FROM instances WHERE status != 99 ORDER BY priority LIMIT 1
        ) ci ON t.task_id = ci.task_id
        LEFT JOIN
            instances hi ON t.task_id = hi.task_id AND hi.status = 99
        WHERE
            t.task_id = ci.task_id
        GROUP BY
            t.task_id;
            """)
    fun getLowestPriorityInstanceWithTask(): LiveData<InstanceWithTask>


}
