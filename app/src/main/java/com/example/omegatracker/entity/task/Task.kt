package com.example.omegatracker.entity.task

import kotlin.time.Duration

interface Task {
    val name: String
    val projectName: String?
    val state: String?
    val workedTime: Duration
    val requiredTime: Duration
    var isRunning: Boolean?
    var dataCreate: Long
}