package com.example.omegatracker.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HistoryData(
    @Embedded val taskData: TaskData,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val historyTasks: List<HistoryTask>
)
