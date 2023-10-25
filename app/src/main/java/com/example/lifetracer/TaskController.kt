package com.example.lifetracer

import android.content.Context

class TaskController(context: Context) {
    private val model = TaskModel(context)

    fun addTask(task: Task) {
        model.addTask(task)
    }

    fun getAllTasks(): List<Task> {
        return model.getAllTasks()
    }
}
