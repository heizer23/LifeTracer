package com.example.lifetracer.data

import androidx.room.Entity

@Entity(tableName = "task_relation", primaryKeys = ["parentId", "subtaskId"])
data class TaskRelation(
    val parentId: Long,
    val subtaskId: Long
)