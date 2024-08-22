package com.example.omegatracker.ui.timer

import com.example.omegatracker.entity.TaskRun
import com.example.omegatracker.ui.base.BaseView

interface TimerView: BaseView {
    fun initialization()
    fun interaction()
    fun backToTasks()
    fun checkUpdateTask()
    fun setView(taskRun: TaskRun)
    fun setTimer(taskRun: TaskRun)
    fun pauseTimer()
    fun setAnimation(newProgress: Float, maxProgress: Float)
}