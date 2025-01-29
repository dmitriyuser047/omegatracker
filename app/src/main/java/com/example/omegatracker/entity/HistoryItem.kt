package com.example.omegatracker.entity

import java.util.Date

interface HistoryItem {
    val historyTaskName: String
    val historyTaskProject: String
    val historyTaskId: String
    val startTime: Long
    val endTime: Long
    val date: Date
}
