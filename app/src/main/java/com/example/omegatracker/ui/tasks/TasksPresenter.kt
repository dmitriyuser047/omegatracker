package com.example.omegatracker.ui.tasks

import android.content.Intent
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksPresenter @Inject constructor(private val repositoryApiImpl: RepositoryImpl) : BasePresenter<TasksView>() {

    private val component = OmegaTrackerApplication.appComponent
    private lateinit var taskController: TasksService.Controller

    init {
        launch {
            repositoryApiImpl.getTasks().collect { list ->
                viewState.showTasks(list)
                list.filter { it.isRunning == true }.forEach {
                    startTask(it)
                }
            }
        }
    }


    fun intentToAuth() {
        component.userManager().exit()
        viewState.showScreen(Screens.AuthScreen)
    }

    fun intentToTimer(intent: Intent, taskRuns: List<TaskRun>) {
        launch {
            repositoryApiImpl.updateTasksBase(taskRuns)
        }
        viewState.intentToTimer(intent)
    }

    fun updateListTasks(list: List<TaskRun>, position: Int): List<TaskRun> {
        list[position].isRunning = true
        val updatedList = ArrayList<TaskRun>().apply {
            add(list[position].copy(isRunning = true))
            addAll(list.filterIndexed { index, _ -> index != position })
        }
         viewState.startTaskTime(list[position])
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
        taskController.serviceDisconnect()
    }

    fun startTask(taskRun: TaskRun) {
        taskController.startTask(taskRun)
        viewState.setNewTasksTime(taskRun)
    }

    fun getUpdateTasksTime(taskRun: TaskRun): Flow<TaskRun> {
        return taskController.getUpdatedTask(taskRun)
    }

}