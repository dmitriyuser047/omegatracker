package com.example.omegatracker.ui.timer

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.ClicksButton
import com.example.omegatracker.entity.NavigationData
import com.example.omegatracker.entity.TaskRun
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
        viewState.showScreen(NavigationData(Screens.TasksScreen, null))
    }

    fun setController(binder: TasksService.Controller) {
        controller = binder
    }

    private fun getCurrentStateOfTask(task: TaskRun): ClicksButton {
        return if (task.isRunning == true) {
            ClicksButton.PAUSE
        } else {
            ClicksButton.COMPLETE
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeForTimer(taskRun: TaskRun): Flow<TaskRun>? {
        return controller?.getUpdatedTask(taskRun.id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pauseTimer(taskRun: TaskRun) {
        launch {
            repositoryImpl.updateTask(taskRun)
        }
        controller?.stopUntilTimeTask(taskRun)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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