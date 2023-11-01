package com.example.lifetracer

import com.example.lifetracer.model.InstanceModel
import com.example.lifetracer.model.TaskModel
import android.content.Context
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.Task

object Controller {

    private lateinit var taskModel: TaskModel
    private lateinit var instanceModel: InstanceModel

    fun initialize(context: Context) {

   //    val dbHelper = com.example.lifetracer.model.MyDatabaseHelper.getInstance(context)
   //    dbHelper.deleteDatabase(context)

        taskModel = TaskModel(context)
        instanceModel = InstanceModel(context)
    }

    fun addTaskAndInstance(task: Task) {
        val newTask = taskModel.addTask(task)

        instanceModel.addEmptyInstance(newTask)
    }

    fun getAllTasks(): List<Task> {
        return taskModel.getAllTasks()
    }

    fun deleteTask(task: Task) {
        taskModel.deleteTask(task)
    }

    fun getAllInstances(): List<Instance> {
        return instanceModel.getAllInstancesWithTasks()
    }

}
