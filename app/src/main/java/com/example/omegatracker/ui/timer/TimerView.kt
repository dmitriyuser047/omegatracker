package com.example.omegatracker.ui.timer

import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.entity.TimerButtons
import com.example.omegatracker.entity.task.State
import com.example.omegatracker.ui.base.BaseView

interface TimerView: BaseView {
    fun initialization()
    fun interaction()
    fun backToTasks()
    fun checkUpdateTask()
    fun setView(taskRun: TaskRun)
    fun setTimer(taskRun: TaskRun)

    fun updateButtonVisibility(currentState: TimerButtons)
    fun setAnimation(newProgress: Float, maxProgress: Float)

    fun changeState(state: State)
    fun buttonActions()
}