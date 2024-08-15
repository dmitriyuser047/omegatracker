package com.example.omegatracker.ui.tasks

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@RequiresApi(Build.VERSION_CODES.O)
class TasksPresenter @Inject constructor(private val repositoryImpl: RepositoryImpl) : BasePresenter<TasksView>() {

    private val component = OmegaTrackerApplication.appComponent
    private lateinit var taskController: TasksService.Controller
    init {
        viewState.startService()
        launch {
            repositoryImpl.getTasks().collect { list ->
                restartTasks(list)
                viewState.showTasks(updateListTasks(list))
            }
        }
    }

    private fun restartTasks(list: List<TaskRun>) {
        list.filter { it.isRunning == true }.forEach {
            taskController.startTask(it)
            getUpdateTasksTime(it)
        }
    }

    fun intentToAuth() {
        component.userManager().exit()
        viewState.showScreen(Screens.AuthScreen)
    }

    fun intentToTimer(intent: Intent, taskRuns: List<TaskRun>) {
        launch {
            repositoryImpl.updateTasksBase(taskRuns)
        }
        viewState.intentToTimer(intent)
    }

    fun updateListTasks(list: List<TaskRun>): List<TaskRun> {
        val updatedList = list.sortedByDescending { it.isRunning }
        return updatedList
    }

    fun updateTimeTasks(list: List<TaskRun>, taskRun: TaskRun): List<TaskRun> {
        val index = list.indexOfFirst { it.id == taskRun.id }
        return if (index != -1) {
            list.toMutableList().apply {
                set(index, taskRun)
            }
        } else {
            list
        }
    }

    fun onServiceConnected(tasksConsole: TasksService.Controller) {
        taskController = tasksConsole
    }

    fun onServiceDisconnected() {
    }

    fun startTask(taskRun: TaskRun) {
        launch {
            repositoryImpl.updateTask(taskRun)
        }
        taskController.startTask(taskRun)
        getUpdateTasksTime(taskRun)
    }

    private fun getUpdateTasksTime(taskRun: TaskRun) {
        launch {
            taskController.getUpdatedTask(taskRun.id).collect {
                viewState.setNewTasksTime(it)
                println("time in presenter - " + it.fullTime)
            }
        }
    }

}