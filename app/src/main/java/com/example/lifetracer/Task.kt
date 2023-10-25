package com.example.lifetracer

data class Task(
    val id: Long,
    val name: String,
    val quality: String,
    val dateOfCreation: String,
    val regularity: Int,
    val fixed: Boolean
)

