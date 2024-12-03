package com.example.lifetracer.Utilities

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
    fun getCurrentTime(): String {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return currentTime.format(Date())
    }


enum class Mode {
    INSTANCES,
    SUBTASKS
}

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("doubleValue")
    fun setDoubleValue(editText: EditText, value: Double?) {
        if (value != null && editText.text.toString() != value.toString()) {
            editText.setText(value.toString())
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "doubleValue")
    fun getDoubleValue(editText: EditText): Double {
        return editText.text.toString().toDoubleOrNull() ?: 0.0
    }

    @JvmStatic
    @BindingAdapter("doubleValueAttrChanged")
    fun setDoubleValueListener(editText: EditText, listener: InverseBindingListener?) {
        if (listener != null) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    listener.onChange()
                }
            })
        }
    }
}