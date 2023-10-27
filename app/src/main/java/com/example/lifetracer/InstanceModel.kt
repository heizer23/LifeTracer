package com.example.lifetracer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class InstanceModel(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "InstanceDatabase"
        const val DATABASE_VERSION = 1
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
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK_ID INTEGER,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_DURATION INTEGER,
                $COLUMN_TOTAL_PAUSE INTEGER,
                $COLUMN_QUANTITY INTEGER,
                $COLUMN_QUALITY TEXT,
                $COLUMN_COMMENT TEXT
            );
        """.trimIndent()
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addInstance(instance: Instance): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_ID, instance.taskId)
        values.put(COLUMN_DATE, instance.date)
        values.put(COLUMN_TIME, instance.time)
        values.put(COLUMN_DURATION, instance.duration)
        values.put(COLUMN_TOTAL_PAUSE, instance.totalPause)
        values.put(COLUMN_QUANTITY, instance.quantity)
        values.put(COLUMN_QUALITY, instance.quality)
        values.put(COLUMN_COMMENT, instance.comment)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun deleteInstance(instance: Instance) {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(instance.id.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getAllInstancesByTaskId(taskId: Long): List<Instance> {
        val instanceList = mutableListOf<Instance>()
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TASK_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(taskId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val taskId = cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_ID))
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                val time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
                val duration = cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION))
                val totalPause = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_PAUSE))
                val quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))
                val quality = cursor.getString(cursor.getColumnIndex(COLUMN_QUALITY))
                val comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT))
                instanceList.add(
                    Instance(id, taskId, date, time, duration, totalPause, quantity, quality, comment)
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return instanceList
    }

    fun getAllInstances(): List<Instance> {
        val instanceList = mutableListOf<Instance>()
        val query = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val taskId = cursor.getLong(cursor.getColumnIndex(COLUMN_TASK_ID))
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                val time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
                val duration = cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION))
                val totalPause = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_PAUSE))
                val quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))
                val quality = cursor.getString(cursor.getColumnIndex(COLUMN_QUALITY))
                val comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT))
                instanceList.add(
                    Instance(id, taskId, date, time, duration, totalPause, quantity, quality, comment)
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return instanceList
    }


}
