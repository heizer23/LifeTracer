package com.example.lifetracer.model


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifetracer.data.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task): Long

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("""
        SELECT * FROM tasks 
            LEFT JOIN instances ON tasks.task_id = instances.task_id
            WHERE NOT (tasks.regularity = 0 AND instances.status = 1)
    """)
    fun getAllTasks():  LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    fun getTaskById(taskId: Long): LiveData<Task>
}