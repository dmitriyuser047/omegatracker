package com.example.omegatracker.ui.timer

import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.entity.TimerButtons
import com.example.omegatracker.entity.task.State
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerPresenter @Inject constructor(private val repositoryImpl: RepositoryImpl)
    : BasePresenter<TimerView>() {

    private var controller: TasksService.Controller? = null

    fun backAction() {
        viewState.navigateTo(Screens.TasksScreen)
    }

    fun setController(binder: TasksService.Controller) {
        controller = binder
    }

    private fun getCurrentStateOfTask(task: TaskRun): TimerButtons {
        return if (task.isRunning == true) {
            viewState.changeState(State.InProgress)
            TimerButtons.START
        } else {
            viewState.changeState(State.Open)
            TimerButtons.COMPLETE
        }
    }

    fun findTaskRun(taskId: String) {
        launch {
            val taskRun = repositoryImpl.getTaskById(taskId)
            if (taskRun != null) {
                repositoryImpl.differenceCheckTaskRun(taskRun).collect { task ->
                    viewState.setView(task)
                    viewState.updateButtonVisibility(getCurrentStateOfTask(task))
                    setProgressBarProgress(task)
                }
            }
        }
    }
    private fun setProgressBarProgress(taskRun: TaskRun) {
        val requiredTime =
            taskRun.requiredTime.inWholeMinutes * 60 * 1000 - taskRun.spentTime.inWholeMilliseconds
        val updateInterval = 1000L
        val maxProgress = (requiredTime / updateInterval).toFloat()
        var initialProgress = (taskRun.spentTime.inWholeMilliseconds / updateInterval).toFloat()

       launch {
            while (taskRun.isRunning == true && initialProgress <= maxProgress) {
                initialProgress++
                viewState.setAnimation(initialProgress, maxProgress)
                delay(updateInterval)
            }
        }

    }

    fun updateTimeForTimer(taskRun: TaskRun) {
        viewState.setTimer(taskRun)
    }

    fun getTimeForTimer(taskRun: TaskRun): Flow<TaskRun>? {
        return controller?.getUpdatedTask(taskRun.id)
    }

    fun pauseTimer(taskRun: TaskRun) {
        launch {
            repositoryImpl.updateTask(taskRun)
        }
        controller?.pauseTask(taskRun)
    }

    fun resumeTimer(taskRun: TaskRun) {
        launch {
            repositoryImpl.updateTask(taskRun)
        }
        controller?.startTask(taskRun)
        viewState.setTimer(taskRun)
        viewState.setView(taskRun)
    }

    fun completeTask(taskRun: TaskRun) {}
}