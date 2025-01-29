package com.example.omegatracker.ui.tasks

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BasePresenter
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksFragmentPresenter @Inject constructor(private val repository: RepositoryImpl) :
    BasePresenter<TasksFragmentView>() {

    private var taskController: TasksService.Controller? = null

    fun getTasksFromData() {
        launch {
            repository.getTasks().collect { list ->
                //restartTasks(list)
                viewState.setTasks(updateListTasks(list))
            }
        }
    }

    fun checkTasksUpdates() {
        launch {
            val tasks = repository.getTasksFromDatabase()
            restartTasks(tasks)
            viewState.setTasks(updateListTasks(tasks))
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

    fun intentToTimer(taskRuns: List<TaskRun>, taskRun: TaskRun) {
        launch {
            repository.updateTasksBase(taskRuns)
        }
        viewState.navigateScreen(Screens.TimerScreen(taskRun))
    }

    fun filterTasksByDate(filter: TaskFilter, tasksRun: List<TaskRun>): List<TaskRun> {
        return when (filter) {
            TaskFilter.AllTasks -> tasksRun
            TaskFilter.Today -> tasksRun.filter { repository.isToday(it.dataCreate) || it.isRunning == true }
        }
    }

    fun updateListTasks(list: List<TaskRun>): List<TaskRun> {
        val updatedList = list.sortedWith(compareByDescending<TaskRun> { it.isRunning }
            .thenByDescending { it.dataCreate })

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
            repository.updateTask(taskRun)
        }
        taskController?.startTask(taskRun)
        getUpdateTasksTime(taskRun)
    }

    private fun getUpdateTasksTime(taskRun: TaskRun) {
        launch {
            taskController?.getUpdatedTask(taskRun.id)?.collect {
                viewState.setNewTasksTime(it)
            }
        }
    }

    fun intentToAuth() {
        OmegaTrackerApplication.appComponent.userManager().exit()
        launch {
            repository.deleteData()
            viewState.navigateScreen(Screens.AuthScreen)
        }
    }
}