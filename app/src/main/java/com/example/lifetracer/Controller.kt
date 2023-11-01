package com.example.lifetracer

import InstanceModel
import TaskModel
import android.content.Context

object Controller {

    private lateinit var taskModel: TaskModel
    private lateinit var instanceModel: InstanceModel

    fun initialize(context: Context) {

   //    val dbHelper = MyDatabaseHelper.getInstance(context)
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
