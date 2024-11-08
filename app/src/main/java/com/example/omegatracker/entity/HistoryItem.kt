package com.example.omegatracker.entity

data class HistoryItem(
    val historyTaskName: String,
    val historyTaskProject: String,
    val historyTaskId: String,
    val startTime: Long,
    val endTime: Long,
    val date: String
)
