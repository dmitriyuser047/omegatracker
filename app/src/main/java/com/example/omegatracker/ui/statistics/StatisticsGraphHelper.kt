package com.example.omegatracker.ui.statistics

import com.example.omegatracker.db.entity.HistoryData
import com.example.omegatracker.utils.formatDurationToGraph
import java.util.Calendar
import java.util.Date

class StatisticsGraphHelper {
    private var historyData: List<HistoryData> = emptyList()

    private val maxDuration: Long
        get() = historyData.maxOfOrNull { it.endTime - it.startTime } ?: 0L

    private val stepDuration: Long
        get() = if (maxDuration > 0) maxDuration / 5 else 0L

    private var scale: Double = 1.15

    fun getYLabels(): List<String> {
        val yLabels = if (maxDuration == 0L) {
            emptyList()
        } else {
            // Округляем maxDuration до ближайшего кратного 30 минутам
            val roundedMaxDuration = (maxDuration / (30 * 60 * 1000)) * (30 * 60 * 1000)

            // Рассчитываем шаг по оси Y на основе округленного значения
            val stepDuration = roundedMaxDuration / 5

            // Генерируем 6 меток для оси Y
            List(6) { formatDurationToGraph((it * stepDuration).toLong()) }
        }
        return yLabels
    }

    fun setHistoryData(newHistoryData: List<HistoryData>) {
        historyData = newHistoryData
    }

    fun getTimeDays(): List<Triple<Int, Long, Long>> {
        return historyData.map { historyItem ->
            val dayOfWeek = getDayOfWeek(historyItem.date)
            val startTime = historyItem.startTime
            val endTime = historyItem.endTime
            Triple(dayOfWeek, startTime, endTime)
        }
    }

    private fun getDayOfWeek(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return if (dayOfWeek == 1) 6 else dayOfWeek - 2
    }
}


