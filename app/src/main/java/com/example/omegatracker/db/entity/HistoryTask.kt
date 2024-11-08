package com.example.omegatracker.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [ForeignKey(
        entity = TaskData::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("taskId"),
        onUpdate = ForeignKey.CASCADE
    )]
)
data class HistoryTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: String,
    val startTime: Long,
    val endTime: Long,
    val date: String
)

