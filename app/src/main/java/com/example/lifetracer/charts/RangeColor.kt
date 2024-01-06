package com.example.lifetracer.charts // Replace with your actual package name

import android.graphics.Color

enum class RangeColor(val color: Int) {
    COLOR1(Color.parseColor("#DDFFDD")), // White
    COLOR2(Color.parseColor("#DDFFDD")), // Very Light Green
    COLOR3(Color.parseColor("#BBFFBB")), // Lighter Green
    COLOR4(Color.parseColor("#99FF99")), // Light Green
    COLOR5(Color.parseColor("#77FF77")), // Less Light Green
    COLOR6(Color.parseColor("#55FF55")), // Medium Light Green
    COLOR7(Color.parseColor("#33FF33")), // Medium Green
    COLOR8(Color.parseColor("#11FF11")), // Medium Dark Green
    COLOR9(Color.parseColor("#009900")), // Darker Green
    COLOR10(Color.parseColor("#006400")); // Dark Green

    companion object {
        private val colors = values()
        fun fromProportion(proportion: Float): Int {
            val index = ((colors.size - 1) * proportion).toInt().coerceIn(0, colors.size - 1)
            return colors[index].color
        }
    }
}
