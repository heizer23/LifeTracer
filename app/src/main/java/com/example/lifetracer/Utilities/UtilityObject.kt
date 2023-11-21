package com.example.lifetracer.Utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

    fun getCurrentDate(): String {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return currentDate.format(Date())
    }
    fun getCurrentTime(): String {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return currentTime.format(Date())
    }