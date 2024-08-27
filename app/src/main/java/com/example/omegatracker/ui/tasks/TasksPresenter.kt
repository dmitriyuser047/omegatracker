package com.example.omegatracker.ui.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.NavigationData
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.launch
import javax.inject.Inject


class TasksPresenter @Inject constructor(private val repositoryImpl: RepositoryImpl) : BasePresenter<TasksView>() {

    private val component = OmegaTrackerApplication.appComponent
    private var taskController: TasksService.Controller? = null
    init {
        launch {
            repositoryImpl.getTasks().collect { list ->
                restartTasks(list)
                viewState.showTasks(updateListTasks(list))
            }
        }
    }

    private fun restartTasks(list: List<TaskRun>) {
        list.filter { it.isRunning == true }.forEach {
            taskController?.startTask(it)
            getUpdateTasksTime(it)
        }
    }

    fun setController(binder: TasksService.Controller) {
        taskController = binder
    }

    fun intentToAuth() {
        component.userManager().exit()
        viewState.showScreen(NavigationData(Screens.AuthScreen,null))
    }

    fun intentToTimer(taskRuns: List<TaskRun>, taskId: String) {
        launch {
            repositoryImpl.updateTasksBase(taskRuns)
        }
        viewState.showScreen(NavigationData(Screens.TimerScreen, taskId))
    }

    fun filterTasksByDate(filter: TaskFilter, tasksRun: List<TaskRun>): List<TaskRun> {
        return when (filter) {
            TaskFilter.AllTasks -> tasksRun
            TaskFilter.Today -> tasksRun.filter { repositoryImpl.isToday(it.dataCreate) || it.isRunning == true}
        }
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
    fun startTask(taskRun: TaskRun) {
        launch {
            repositoryImpl.updateTask(taskRun)
        }
        taskController?.startTask(taskRun)
        getUpdateTasksTime(taskRun)
    }

    private fun getUpdateTasksTime(taskRun: TaskRun) {
        launch {
            taskController?.getUpdatedTask(taskRun.id)?.collect {
                viewState.setNewTasksTime(it)
                println("time in presenter - " + it.fullTime)
            }
        }
    }

}