package com.example.omegatracker.service

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.entity.task.TaskFromJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import okhttp3.internal.concurrent.TaskRunner
import javax.inject.Singleton

@Singleton
class TasksManager {

    private val taskRunners: MutableMap<String, TasksRunner> = mutableMapOf()

    fun launchTaskRunner(taskRun: TaskRun) {
        if (!taskRunners.containsKey(taskRun.id)) {
            val newTasksRunner = OmegaTrackerApplication.appComponent.tasksRunner()
            taskRunners[taskRun.id] = newTasksRunner
            newTasksRunner.launchTask(taskRun)
        } else {
            taskRunners[taskRun.id]?.restartTask(taskRun)
        }
    }

    fun stopUntilTimeTaskRunner(taskRun: TaskRun) {
        taskRunners[taskRun.id]?.stopTask(taskRun)
    }

    fun getTaskUpdates(tasksId: String): Flow<TaskRun> {
        return taskRunners[tasksId]!!.taskUpdates.filter { it.id == tasksId  }
    }

    fun getCompletedTask(task: TaskFromJson) {
    }

}
