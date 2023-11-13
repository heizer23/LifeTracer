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

    @Query("SELECT * FROM tasks")
    fun getAllTasks():  LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    fun getTaskById(taskId: Long): LiveData<Task>
}