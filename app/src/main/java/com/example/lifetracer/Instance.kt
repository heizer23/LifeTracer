package com.example.lifetracer

data class Instance(
    val id: Long,
    val taskId: Long,
    val taskName: String,
    val taskQuality: String,
    val taskDateOfCreation: String,
    val date: String,
    val time: String,
    val duration: Int,
    val totalPause: Int,
    val quantity: Int,
    val quality: String,
    val comment: String,
    val status: Int
){
    companion object {
        const val STATUS_PLANNED = 0
        const val STATUS_STARTED = 1
        const val STATUS_PAUSED = 2
        const val STATUS_FINISHED = 3
    }
}