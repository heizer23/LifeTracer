package com.example.lifetracer.charts

data class BarEntryData(
    val date: String,
    val quantity: Double
) {
    companion object {
        fun parseHistoricalData(historicalData: String): List<BarEntryData> {
            return historicalData.split(",").mapNotNull { pairString ->
                pairString.split("_").let {
                    if (it.size >= 2) BarEntryData(it[0], it[1].toDoubleOrNull() ?: 1.0) else null
                }
            }
        }
    }
}
