package com.example.omegatracker.entity

import com.example.omegatracker.entity.task.Task
import java.io.Serializable
import kotlin.time.Duration

data class TaskRun(
    val id: String,
    var startTime: Duration,
    val description: String?,
    override val projectName: String?,
    override var state: String?,
    override val workedTime: Duration,
    override val requiredTime: Duration,
    override var isRunning: Boolean?,
    override val name: String,
    var spentTime: Duration,
    var fullTime: Duration,
    override var dataCreate: Long
): Task, Serializable
