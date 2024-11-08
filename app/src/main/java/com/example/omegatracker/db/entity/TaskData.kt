package com.example.omegatracker.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omegatracker.entity.task.Task
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Entity
data class TaskData(
    @PrimaryKey val id: String,
    @ColumnInfo("description")
    val description: String?,
    @ColumnInfo("name")
    override val name: String,
    @ColumnInfo("projectName")
    override val projectName: String?,
    @ColumnInfo("state")
    override val state: String,
    @ColumnInfo("workedTime")
    val workedTimeLong: Long,
    @ColumnInfo("requiredTime")
    val requiredTimeLong: Long,
    @ColumnInfo("isRunning")
    override var isRunning: Boolean?,
    @ColumnInfo("startTime")
    var startTimeLong: Long,
    @ColumnInfo("fullTime")
    var endTimeLong: Long,
    override var dataCreate: Long,
    @ColumnInfo("iconUrl")
    override val imageUrl: String?
) : Task {
    override val workedTime: Duration
        get() = workedTimeLong.toDuration(DurationUnit.MINUTES)
    override val requiredTime: Duration
        get() = requiredTimeLong.toDuration(DurationUnit.MINUTES)
}