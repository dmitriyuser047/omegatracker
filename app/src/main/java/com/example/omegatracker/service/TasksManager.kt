package com.example.omegatracker.service

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.entity.task.Task
import com.example.omegatracker.entity.task.TaskFromJson
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

@Singleton
class TasksManager {

    private val tasksRunners: MutableMap<String, TasksRunner> = mutableMapOf()

    fun launchTaskRunner(taskRun: TaskRun) {
        if (!tasksRunners.containsKey(taskRun.id)) {
            val taskRunner = OmegaTrackerApplication.appComponent.tasksRunner()
            tasksRunners[taskRun.id] = taskRunner
            taskRunner.launchTask(taskRun)
        }
    }

    fun stopUntilTimeTaskRunner(taskRun: TaskRun) {
        tasksRunners[taskRun.id]?.stopTask(taskRun)
        tasksRunners.remove(taskRun.id)
    }

    fun getTaskUpdates(taskId: String): Flow<TaskRun> {
        return tasksRunners[taskId]?.taskUpdates ?: flowOf()
    }

}
