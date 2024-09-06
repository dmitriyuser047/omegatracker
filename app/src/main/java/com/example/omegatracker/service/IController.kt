package com.example.omegatracker.service

import com.example.omegatracker.entity.task.TaskRun
import kotlinx.coroutines.flow.Flow

interface IController {
    fun startTask(taskRun: TaskRun)
    fun getUpdatedTask(taskId: String): Flow<TaskRun>
    fun pauseTask(taskRun: TaskRun)
}