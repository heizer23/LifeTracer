package com.example.lifetracer.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.lifetracer.charts.RangeColor
import com.github.mikephil.charting.data.BarEntry

class WeekChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var weekData: List<Pair<Float, Int>> = listOf() // List of pairs (instances, week)

    fun setWeekData(data: List<BarEntry>) {
        // Map BarEntry list to Pair list
        weekData = data.map { Pair(it.y, extractWeek(it.x)) }
        invalidate() // Redraw the view when data changes
    }

    private fun extractWeek(floatWeek: Float): Int {
        // Extracts the week part from the float representation
        val weekAsString = "%.2f".format(floatWeek)
        return weekAsString.substringAfter(".").toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rectWidth = width / 52f // Assuming 52 weeks in a year

        weekData.forEach { (instances, week) ->
            val proportion = instances / 7f // Assuming '7' is the max value
            val paint = Paint().apply {
                color = RangeColor.fromProportion(proportion)
                style = Paint.Style.FILL
            }
            val left = (week - 1) * rectWidth
            canvas.drawRect(
                left, // left
                0f, // top
                left + rectWidth, // right
                height.toFloat(), // bottom
                paint
            )
        }
    }
}

