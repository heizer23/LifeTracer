package com.example.lifetracer.model


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifetracer.data.Task
import com.example.lifetracer.data.TaskRelation

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task): Long

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("""
        SELECT * FROM tasks where task_id is NULL
    """)
    fun getAllTasks():  LiveData<List<Task>>

    @Query("""
        SELECT 
            t.task_id,
            t.name,
            t.task_quality,
            t.date_of_creation,
            t.regularity,
            t.fixed,
            t.input_type
        FROM 
            tasks t
        LEFT JOIN 
            instances i ON t.task_id = i.task_id
        GROUP BY 
            t.task_id
        HAVING 
            (t.regularity = 0 AND COUNT(i.id) = 0)
            OR (t.regularity != 0 AND SUM(CASE WHEN i.status != 99 THEN 1 ELSE 0 END) = 0);
    """)
    fun getAllTasksWithoutInstance():  LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    fun getTaskById(taskId: Long): LiveData<Task>


    // Subtasks

    // Insert relationship
    @Insert
    suspend fun insertTaskRelation(taskRelation: TaskRelation)

    // Query subtasks for a specific parent
    @Query("SELECT tasks.* FROM tasks INNER JOIN task_relation ON tasks.task_id = task_relation.subtaskId WHERE task_relation.parentId = :parentId")
    suspend fun getSubtasksForParent(parentId: Long): List<Task>




}