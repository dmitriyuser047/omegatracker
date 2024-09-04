package com.example.omegatracker.entity

import android.os.Parcelable
import com.example.omegatracker.entity.task.Task
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlin.time.Duration

@Parcelize
data class TaskRun(
    val id: String,
    var startTime: @RawValue Duration,
    val description: String?,
    override val projectName: String?,
    override var state: String?,
    override val workedTime: @RawValue Duration,
    override val requiredTime: @RawValue Duration,
    override var isRunning: Boolean?,
    override val name: String,
    var spentTime:  @RawValue Duration,
    var fullTime:  @RawValue Duration,
    override var dataCreate: Long
): Task, Parcelable
//уточнить