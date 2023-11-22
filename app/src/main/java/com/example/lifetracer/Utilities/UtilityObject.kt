package com.example.lifetracer.Utilities

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
    fun getCurrentTime(): String {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return currentTime.format(Date())
    }
