package com.example.omegatracker.service

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.entity.task.TaskRun
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Singleton

@Singleton
class TasksManager {

    private val tasksRunners: MutableMap<String, TasksRunner> = mutableMapOf()
    private val taskRuns: MutableMap<String, TaskRun> = mutableMapOf()

    fun launchTaskRunner(taskRun: TaskRun) {
        if (!tasksRunners.containsKey(taskRun.id)) { // Проверяем наличие в tasksRunners
            tasksRunners[taskRun.id] = TasksRunner()
            taskRuns[taskRun.id] = taskRun
            tasksRunners[taskRun.id]?.launchTask(taskRun)
        } else {
            println("TaskRunner for task ${taskRun.id} already exists.")
        }
    }

    fun pauseTaskRunner(taskRun: TaskRun) {
        tasksRunners[taskRun.id]?.let { taskRunner ->
            taskRunner.stopTask(taskRun)
            tasksRunners.remove(taskRun.id)
            taskRuns.remove(taskRun.id)
        }
    }

    fun pauseAllTasksRunners() {
        tasksRunners.forEach {
            tasksRunners.remove(it.key)
        }
        taskRuns.forEach {
            taskRuns.remove(it.key)
        }
    }

    fun playAllTasksRunners() {
        taskRuns.forEach { (id, taskRun) ->
            val taskRunner = OmegaTrackerApplication.appComponent.tasksRunner()
            tasksRunners[id] = taskRunner
            taskRunner.launchTask(taskRun)
        }
    }

    fun getTaskUpdates(taskId: String): Flow<TaskRun> {
        println("TasksManager updates ")
        return tasksRunners[taskId]?.taskUpdates ?: flowOf()
    }

}
