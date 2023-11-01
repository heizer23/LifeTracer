package com.example.lifetracer

data class Instance(
    val id: Long,             // Primary key for the instance
    val taskId: Long,         // References the task by its primary id
    val taskName: String,     // Name of the associated task
    val taskQuality: String,  // Quality of the associated task
    val taskDateOfCreation: String, // Date of task creation
    val date: String,         // Date of the instance
    val time: String,         // Time of the instance
    val duration: Int,        // Duration of the instance (in minutes)
    val totalPause: Int,      // Total pause during the instance (in minutes)
    val quantity: Int,       // Quantity of the task for this instance
    val quality: String,     // Quality of the instance
    val comment: String      // Comment for the instance
)
