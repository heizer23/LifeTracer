package com.example.lifetracer.model

import android.content.ContentValues
import android.content.Context
import com.example.lifetracer.data.Task

class TaskModel(context: Context) {

    private val databaseHelper: MyDatabaseHelper = MyDatabaseHelper.getInstance(context)

    fun addTask(task: Task): Task {
        val db = databaseHelper.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, task.name)
        values.put(COLUMN_QUALITY, task.taskQuality)
        values.put(COLUMN_DATE_CREATION, task.dateOfCreation)
        values.put(COLUMN_REGULARITY, task.regularity)
        values.put(COLUMN_FIXED, if (task.fixed) 1 else 0)
        val taskId = db.insert(TABLE_NAME, null, values)
        db.close()

        // Update the id because so far it only has "0", this is used when an instance is directly created in the next step
        task.updateId(taskId)
        return task
    }

    fun deleteTask(task: Task) {
        val db = databaseHelper.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(task.id.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val query = "SELECT * FROM $TABLE_NAME"
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val quality = cursor.getString(cursor.getColumnIndex(COLUMN_QUALITY))
                val dateOfCreation = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATION))
                val regularity = cursor.getInt(cursor.getColumnIndex(COLUMN_REGULARITY))
                val fixed = cursor.getInt(cursor.getColumnIndex(COLUMN_FIXED)) == 1
                taskList.add(Task(id, name, quality, dateOfCreation, regularity, fixed))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return taskList
    }

    companion object {
        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_QUALITY = "quality"
        const val COLUMN_DATE_CREATION = "date_of_creation"
        const val COLUMN_REGULARITY = "regularity"
        const val COLUMN_FIXED = "fixed"
    }
}
