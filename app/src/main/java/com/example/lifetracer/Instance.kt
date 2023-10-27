package com.example.lifetracer

data class Instance(
    val id: Long,             // Primary key for the instance
    val taskId: Long,         // References the task by its primary id
    val date: String,         // Date of the instance
    val time: String,         // Time of the instance
    val duration: Int,        // Duration of the instance (in minutes)
    val totalPause: Int,      // Total pause during the instance (in minutes)
    val quantity: Int,       // Quantity of the task for this instance
    val quality: String,     // Quality as a string
    val comment: String      // Comment for the instance
)
