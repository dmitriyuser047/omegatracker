package com.example.omegatracker.service

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.entity.task.TaskFromJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import javax.inject.Singleton

@Singleton
class TasksManager {

    private var tasksRunner: TasksRunner

    init {
        tasksRunner = OmegaTrackerApplication.appComponent.tasksRunner()
    }

    fun launchTaskRunner(taskRun: TaskRun) {
        tasksRunner = OmegaTrackerApplication.appComponent.tasksRunner()
        tasksRunner.launchTask(taskRun)
    }

    fun stopUntilTimeTaskRunner(taskRun: TaskRun) {
        tasksRunner.stopTask(taskRun)
    }

    fun getTaskUpdates(tasksId: String): Flow<TaskRun> {
        return tasksRunner.taskUpdates.filter { it.id == tasksId }
    }

    fun getCompletedTask(task: TaskFromJson) {
    }

}
