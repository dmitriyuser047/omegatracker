package com.example.omegatracker.service

import com.example.omegatracker.entity.TaskRun
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface IController {
    fun startTask(taskRun: TaskRun)
    fun getUpdatedTask(taskRun: TaskRun): Flow<TaskRun>
    fun stopUntilTimeTask(taskRun: TaskRun)
    fun handleCompletedTask(taskRun: TaskRun)
    fun serviceDisconnect()
}