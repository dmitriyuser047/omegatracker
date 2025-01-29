package com.example.omegatracker.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.omegatracker.entity.HistoryItem
import java.util.Date

data class HistoryData(
    @Embedded val historyTask: HistoryTask,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "id"
    )
    val taskData: TaskData
): HistoryItem {
    override val historyTaskName: String
        get() = taskData.name

    override val historyTaskProject: String
        get() = taskData.projectName ?: "No project"

    override val historyTaskId: String
        get() = taskData.id

    override val startTime: Long
        get() = historyTask.startTime

    override val endTime: Long
        get() =  historyTask.endTime

    override val date: Date
        get() = Date(historyTask.date)
}
