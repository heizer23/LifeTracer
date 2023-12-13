package com.example.lifetracer.charts

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale


class ChartManager(private val chart: BarChart) {

    // Now setupChart takes a List<BarEntry> directly
    fun setupChart(entries: List<BarEntry>, label: String) {
        val dataSet = BarDataSet(entries, label).apply {
            // Customize the appearance of the bars
            setDrawValues(false)
            // Additional customization can go here
        }

        val barData = BarData(dataSet)
        chart.data = barData

        chart.invalidate() // Refresh the chart
    }

    fun configureChartAppearance() {
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
    }


    fun parseToBarEntries(historicalData: String): Flow<List<BarEntry>> = flow {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val referenceDate = dateFormat.parse("2020-01-01") ?: return@flow
        val entries = mutableListOf<BarEntry>()

        historicalData.split(",").forEachIndexed { index, pairString ->
            pairString.split("_").let {
                if (it.size >= 2) {
                    val date = dateFormat.parse(it[0])
                    val quantity = it[1].toFloatOrNull()
                    if (date != null && quantity != null) {
                        val daysSinceReference =
                            ((date.time - referenceDate.time) / (1000 * 60 * 60 * 24)).toFloat()
                        entries.add(BarEntry(daysSinceReference, quantity))
                    }
                }
            }
            // Emit partial results at intervals (e.g., every 10 entries)
            if (index % 2 == 0) {
                emit(entries.toList()) // Emit a copy of the current list
            }
        }

        // Emit final result
        emit(entries.toList())
    }
}