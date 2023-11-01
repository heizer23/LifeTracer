package com.example.lifetracer.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.example.lifetracer.data.Instance
import com.example.lifetracer.data.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InstanceModel(context: Context) {

    private val databaseHelper: MyDatabaseHelper = MyDatabaseHelper.getInstance(context)

    fun addInstance(instance: Instance): Long {
        val db = databaseHelper.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_ID, instance.taskId)
        values.put(COLUMN_DATE, instance.date)
        values.put(COLUMN_TIME, instance.time)
        values.put(COLUMN_DURATION, instance.duration)
        values.put(COLUMN_TOTAL_PAUSE, instance.totalPause)
        values.put(COLUMN_QUANTITY, instance.quantity)
        values.put(COLUMN_QUALITY, instance.quality)
        values.put(COLUMN_COMMENT, instance.comment)
        values.put(COLUMN_STATUS, instance.status)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun deleteInstance(instance: Instance) {
        val db = databaseHelper.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(instance.id.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllInstancesWithTasks(): List<Instance> {
        val instanceList = mutableListOf<Instance>()
        val query = """
        SELECT 
            instances.id AS instanceId,
            instances.task_Id,
            tasks.name AS taskName,
            tasks.quality AS taskQuality,
            tasks.date_of_creation AS taskDateOfCreation,
            instances.date,
            instances.time,
            instances.duration,
            instances.total_pause,
            instances.quantity,
            instances.quality,
            instances.comment, 
            instances.status
        FROM $TABLE_NAME AS instances
        LEFT JOIN ${TaskModel.TABLE_NAME} AS tasks ON instances.$COLUMN_TASK_ID = tasks.${TaskModel.COLUMN_ID};
    """.trimIndent()

        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery(query, null)

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val instanceId = cursor.getLong(cursor.getColumnIndex("instanceId"))
                val taskId = cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_ID))
                val taskName = cursor.getString(cursor.getColumnIndex("taskName"))
                val taskQuality = cursor.getString(cursor.getColumnIndex("taskQuality"))
                val taskDateOfCreation = cursor.getString(cursor.getColumnIndex("taskDateOfCreation"))
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                val time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
                val duration = cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION))
                val totalPause = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_PAUSE))
                val quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))
                val quality = cursor.getString(cursor.getColumnIndex(COLUMN_QUALITY))
                val comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT))
                val status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))

                val instance = Instance(
                    instanceId,
                    taskId,
                    taskName,
                    taskQuality,
                    taskDateOfCreation,
                    date,
                    time,
                    duration,
                    totalPause,
                    quantity,
                    quality,
                    comment,
                    status
                )

                instanceList.add(instance)
            }
        }

        return instanceList
    }


    fun addEmptyInstance(task: Task): Long {

        val instance = Instance(
            id = 0,
            taskName = "",
            taskQuality = "",
            taskDateOfCreation ="",
            taskId = task.id,
            date = getCurrentDate(),
            time = getCurrentTime(),
            duration = 0,
            totalPause = 0,
            quantity = 0,
            quality = "",
            comment = "",
            status = Instance.STATUS_PLANNED
        )

        return addInstance(instance)
    }


    fun getCurrentDate(): String {
        // Use your preferred date format here
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return currentDate.format(Date())
    }

    fun getCurrentTime(): String {
        // Use your preferred time format here
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return currentTime.format(Date())
    }


    companion object {
        const val TABLE_NAME = "instances"
        const val COLUMN_ID = "id"
        const val COLUMN_TASK_ID = "task_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_TOTAL_PAUSE = "total_pause"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_QUALITY = "quality"
        const val COLUMN_COMMENT = "comment"
        const val COLUMN_STATUS = "status"
    }
}
