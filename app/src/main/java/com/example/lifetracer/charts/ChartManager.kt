package com.example.lifetracer.charts

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class ChartManager(private val chart: BarChart) {
    fun setupChart(data: List<BarEntry>, label: String) {
        val dataSet = BarDataSet(data, label).apply {
            // Setting the visibility of the value labels on each bar
            setDrawValues(false)

            // Customization of the bar appearance can go here
            // ...
        }

        val barData = BarData(dataSet)

        chart.data = barData

        // Remove grid lines and axis labels
        chart.axisLeft.apply {
            setDrawLabels(false) // Remove y-axis labels
            setDrawGridLines(false) // Remove grid lines
            setDrawAxisLine(false) // Remove axis line
        }

        chart.xAxis.apply {
            setDrawLabels(false) // Remove x-axis labels
            setDrawGridLines(false) // Remove grid lines
            setDrawAxisLine(false) // Keep the axis line
        }

        chart.axisRight.isEnabled = false // Disable the right y-axis

        // Remove description label and legend
        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        // Additional customization can be added here
        // ...

        chart.invalidate() // Refresh the chart
    }

    // Add other chart-related methods as needed
}
