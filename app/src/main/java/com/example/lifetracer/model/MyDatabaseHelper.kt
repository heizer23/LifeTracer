package com.example.lifetracer.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyAppDatabase"
        private const val DATABASE_VERSION = 1
        private var instance: MyDatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context): MyDatabaseHelper {
            if (instance == null) {
                instance = MyDatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    fun deleteDatabase(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the "tasks" table
        val createTasksTableSQL = """
            CREATE TABLE ${TaskModel.TABLE_NAME} (
                ${TaskModel.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${TaskModel.COLUMN_NAME} TEXT,
                ${TaskModel.COLUMN_QUALITY} TEXT,
                ${TaskModel.COLUMN_DATE_CREATION} TEXT,
                ${TaskModel.COLUMN_REGULARITY} INTEGER,
                ${TaskModel.COLUMN_FIXED} INTEGER
            );
        """.trimIndent()
        db.execSQL(createTasksTableSQL)

        // Create the "instances" table
        val createInstancesTableSQL = """
            CREATE TABLE ${InstanceModel.TABLE_NAME} (
                ${InstanceModel.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${InstanceModel.COLUMN_TASK_ID} INTEGER,
                ${InstanceModel.COLUMN_DATE} TEXT,
                ${InstanceModel.COLUMN_TIME} TEXT,
                ${InstanceModel.COLUMN_DURATION} INTEGER,
                ${InstanceModel.COLUMN_TOTAL_PAUSE} INTEGER,
                ${InstanceModel.COLUMN_QUANTITY} INTEGER,
                ${InstanceModel.COLUMN_QUALITY} TEXT,
                ${InstanceModel.COLUMN_COMMENT} TEXT,
                ${InstanceModel.COLUMN_STATUS} INTEGER
            );
        """.trimIndent()
        db.execSQL(createInstancesTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop the tables and recreate them (you can implement migrations if needed)
        db.execSQL("DROP TABLE IF EXISTS ${TaskModel.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${InstanceModel.TABLE_NAME}")
        onCreate(db)
    }
}
