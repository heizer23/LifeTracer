package com.example.lifetracer.charts

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.text.SimpleDateFormat
import java.util.Locale

class ChartManager(private val chart: BarChart) {
    fun setupChart(dataString: String, label: String) {
        var data: List<BarEntry> = parseToBarEntries(dataString)

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

    fun parseToBarEntries(historicalData: String): List<BarEntry> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val referenceDate = dateFormat.parse("2020-01-01") // Reference date for converting to float

        return historicalData.split(",").mapIndexedNotNull { index, pairString ->
            pairString.split("_").let {
                if (it.size >= 2) {
                    val date = dateFormat.parse(it[0])
                    val quantity = it[1].toFloatOrNull()
                    if (date != null && quantity != null) {
                        // Convert date to days since reference date
                        val daysSinceReference = ((date.time - referenceDate.time) / (1000 * 60 * 60 * 24)).toFloat()
                        BarEntry(daysSinceReference, quantity)
                    } else null
                } else null
            }
        }
    }
}
