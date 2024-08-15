package com.example.omegatracker.service

import com.example.omegatracker.entity.TaskRun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


class TasksRunner {
    private val _taskUpdates = MutableSharedFlow<TaskRun>()
    val taskUpdates: SharedFlow<TaskRun> = _taskUpdates
    private val foregroundTasks = mutableMapOf<String, Job>()

    fun launchTask(taskRun: TaskRun) {
        taskRun.isRunning = true
        taskRun.startTime = System.currentTimeMillis().milliseconds - taskRun.spentTime
        val job = CoroutineScope(Dispatchers.Default).launch {
            while (taskRun.isRunning == true) {
                taskRun.spentTime = System.currentTimeMillis().milliseconds - taskRun.startTime
                taskRun.fullTime = taskRun.workedTime + taskRun.spentTime
                println("Full time in runner = " + taskRun.fullTime)
                _taskUpdates.emit(taskRun)
                delay(1000)
            }
        }
        foregroundTasks[taskRun.id] = job
    }
    fun stopTask(taskRun: TaskRun) {
        foregroundTasks[taskRun.id]?.let {
            it.cancel()
            taskRun.spentTime = (System.currentTimeMillis().milliseconds - taskRun.startTime)
            foregroundTasks.remove(taskRun.id)
        }
    }
}