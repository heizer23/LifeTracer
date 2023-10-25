package com.example.lifetracer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskModel(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "TaskDatabase"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_QUALITY = "quality"
        const val COLUMN_DATE_CREATION = "date_of_creation"
        const val COLUMN_REGULARITY = "regularity"
        const val COLUMN_FIXED = "fixed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_QUALITY TEXT,
                $COLUMN_DATE_CREATION TEXT,
                $COLUMN_REGULARITY INTEGER,
                $COLUMN_FIXED INTEGER
            );
        """.trimIndent()
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(task: Task) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, task.name)
        values.put(COLUMN_QUALITY, task.quality)
        values.put(COLUMN_DATE_CREATION, task.dateOfCreation)
        values.put(COLUMN_REGULARITY, task.regularity)
        values.put(COLUMN_FIXED, if (task.fixed) 1 else 0)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun deleteTask(task: Task) {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(task.id.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val query = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
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
}