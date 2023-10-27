package com.example.lifetracer

import android.content.Context
object Controller {
    private lateinit var taskModel: TaskModel
    private lateinit var instanceModel: InstanceModel

    fun initialize(context: Context) {
        taskModel = TaskModel(context)
        instanceModel = InstanceModel(context)
    }

    fun addTaskAndInstance(task: Task) {
        val taskId = taskModel.addTask(task)

        val instance = Instance(
            0,
            taskId = taskId,
            date = "2023-10-26",   // Dummy date
            time = "08:00 AM",     // Dummy time
            duration = 60,         // Dummy duration in minutes
            totalPause = 0,        // Dummy total pause in minutes
            quantity = 1,          // Dummy quantity
            quality = "Good",     // Dummy quality
            comment = "Dummy comment"
        )

        instanceModel.addInstance(instance)
    }

    fun getAllTasks(): List<Task> {
        return taskModel.getAllTasks()
    }

    fun deleteTask(task: Task) {
        taskModel.deleteTask(task)
    }

    fun getAllInstances(): List<Instance> {
        return instanceModel.getAllInstances()
    }

}
